/*
 *
 *    Copyright (C) 2020 Joffrey Bonifay
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.joffrey.iracing.irsdkjava.laptiming;

import com.joffrey.iracing.irsdkjava.config.FluxProperties;
import com.joffrey.iracing.irsdkjava.laptiming.model.LapTimingData;
import com.joffrey.iracing.irsdkjava.laptiming.model.LapTimingData.LiveData;
import com.joffrey.iracing.irsdkjava.laptiming.model.LapTimingData.YamlData;
import com.joffrey.iracing.irsdkjava.model.SdkStarter;
import com.joffrey.iracing.irsdkjava.model.defines.TrkLoc;
import com.joffrey.iracing.irsdkjava.yaml.YamlService;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import com.joffrey.iracing.irsdkjava.yaml.irsdkyaml.DriverInfoYaml;
import com.joffrey.iracing.irsdkjava.yaml.irsdkyaml.DriversInfoYaml;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Log
@Service
public class LapTimingService {

    private final FluxProperties fluxProperties;
    private final SdkStarter     sdkStarter;
    private final YamlService    yamlService;

    private final ConnectableFlux<List<LapTimingData>> listLapTimingDataFlux;

    public LapTimingService(FluxProperties fluxProperties, SdkStarter sdkStarter, YamlService yamlService) {
        this.fluxProperties = fluxProperties;
        this.sdkStarter = sdkStarter;
        this.yamlService = yamlService;
        this.listLapTimingDataFlux = Flux.interval(Duration.ofMillis(fluxProperties.getLapTimingIntervalInMs()))
                                         .filter(aLong -> sdkStarter.isRunning()).flatMap(aLong -> loadLapTimingDataList())
                                         .publish();
    }

    /**
     * Used to connect to the {@literal Flux<List<LapTimingData>>}
     *
     * @return the {@literal Flux<List<LapTimingData>>} {@link ConnectableFlux}
     */
    public Flux<List<LapTimingData>> getLapTimingDataListFlux() {
        return listLapTimingDataFlux.autoConnect();
    }

    /**
     * Get a list of {@link LapTimingData} object filled with each car data
     */
    private Flux<List<LapTimingData>> loadLapTimingDataList() {
        DriversInfoYaml driverInfo = yamlService.getYamlFile().getDriverInfo();
        int totalSize = driverInfo == null || driverInfo.getDrivers() == null ? 0 : driverInfo.getDrivers().size();
        if (totalSize == 0) {
            return Flux.just(new ArrayList<>());
        }
        return Flux.range(0, totalSize)
                .subscribeOn(Schedulers.parallel())
                .flatMap(carIdx -> getLapTimingDataForCarIdx(carIdx, driverInfo))
                .sort(getLapTimingDataComparator())
                .buffer(totalSize)
                .map(this::setDriversNewPosition)
                .map(this::setDriversInterval);
    }

    /**
     * Get lap timing data for a given car idx
     *
     * @param carIdx the car idx
     * @param driverInfo
     * @return {@link LapTimingData} filled with values
     */
    private Flux<LapTimingData> getLapTimingDataForCarIdx(int carIdx, DriversInfoYaml driverInfo) {
        LapTimingData lapTimingData = new LapTimingData();
        lapTimingData.setCarIdx(carIdx);

        lapTimingData.setLiveData(new LiveData()
                .setCarIdxPosition(sdkStarter.getVarInt("CarIdxPosition", carIdx))
                .setCarIdxClassPosition(sdkStarter.getVarInt("CarIdxClassPosition", carIdx))
                .setCarIdxEstTime(sdkStarter.getVarFloat("CarIdxEstTime", carIdx))
                .setCarIdxF2Time(sdkStarter.getVarFloat("CarIdxF2Time", carIdx))
                .setCarIdxLap(sdkStarter.getVarInt("CarIdxLap", carIdx))
                .setCarIdxLapDistPct(sdkStarter.getVarFloat("CarIdxLapDistPct", carIdx))
                .setCarIdxLastLapTime(sdkStarter.getVarFloat("CarIdxLastLapTime", carIdx))
                .setCarIdxBestLapTime(sdkStarter.getVarFloat("CarIdxBestLapTime", carIdx))
        );

        DriverInfoYaml driverInfoYaml = driverInfo.getDrivers().get(carIdx);
        lapTimingData.setYamlData(new YamlData()
                .setCarIdxTrackSurface(TrkLoc.valueOf(sdkStarter.getVarInt("CarIdxTrackSurface", carIdx)))
                .setCarIsPaceCar(driverInfoYaml.getCarIsPaceCar())
                .setCarIsAI(driverInfoYaml.getCarIsAI())
                .setUserName(driverInfoYaml.getUserName())
                .setTeamName(driverInfoYaml.getTeamName())
                .setCarNumber(driverInfoYaml.getCarNumber())
                .setCarId(driverInfoYaml.getCarID())
                .setCarClassID(driverInfoYaml.getCarClassID())
                .setCarClassColor(driverInfoYaml.getCarClassColor())
                .setIRating(driverInfoYaml.getIRating())
                .setLicLevel(driverInfoYaml.getLicLevel())
                .setLicString(driverInfoYaml.getLicString())
                .setLicColor(driverInfoYaml.getLicColor())
                .setIsSpectator(driverInfoYaml.getIsSpectator())
                .setClubName(driverInfoYaml.getClubName())
                .setDivisionName(driverInfoYaml.getDivisionName())
        );

        return Flux.just(lapTimingData);
    }

    /**
     * Sort {@link LapTimingData} flux for place drivers with pos = 0 at the end Sort by position and DistPct
     *
     * @return a comparator
     */
    private Comparator<LapTimingData> getLapTimingDataComparator() {
        return (o1, o2) -> {
            // Check for drivers track pct
            if (o1.getLiveData().getCarIdxLap() == o2.getLiveData().getCarIdxLap()) {
                if (o1.getLiveData().getCarIdxLapDistPct() < o2.getLiveData().getCarIdxLapDistPct()) {
                    return 1;
                } else if (o1.getLiveData().getCarIdxLapDistPct() == o2.getLiveData().getCarIdxLapDistPct()) {
                    return 0;
                }
            } else {
                // Put all pos = 0 at end of array
                if (o1.getLiveData().getCarIdxLap() > o2.getLiveData().getCarIdxLap()) {
                    return -1;
                } else if (o1.getLiveData().getCarIdxLap() == o2.getLiveData().getCarIdxLap()) {
                    return 0;
                } else {
                    return 1;
                }
            }
            return -1;
        };
    }

    /**
     * After sorting drivers with getLapTimingDataForCarIdx, position are still unordered
     *
     * @param lapTimingData the {@link LapTimingData} list to modify
     * @return a {@link LapTimingData} list modified
     */
    private List<LapTimingData> setDriversNewPosition(List<LapTimingData> lapTimingData) {
        IntStream.range(0, lapTimingData.size()).forEachOrdered(i -> lapTimingData.get(i).setCarLivePosition(i + 1));
        return lapTimingData;
    }

    /**
     * Set the interval between drivers
     *
     * @param lapTimingData the list containing all drivers
     * @return the entry list filled with interval values
     */
    private List<LapTimingData> setDriversInterval(List<LapTimingData> lapTimingData) {
        IntStream.range(0, lapTimingData.size()).forEachOrdered(i -> {
            float time = 0.0f;
            if (i != 0) {
                LapTimingData prevDriver = lapTimingData.get(i - 1);
                time = Math.abs(prevDriver.getLiveData().getCarIdxEstTime() - lapTimingData.get(i).getLiveData().getCarIdxEstTime());
            }
            lapTimingData.get(i).setCarIntervalWithPreviousCar(time);
        });

        return lapTimingData;
    }

}
