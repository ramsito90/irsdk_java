/*
 *
 *    Copyright (C) 2020 Joffrey Bonifay
 *    Copyright (C) 2021 Ramsés Corporales
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

package com.joffrey.iracing.irsdkjava.telemetry;

import com.joffrey.iracing.irsdkjava.config.FluxProperties;
import com.joffrey.iracing.irsdkjava.model.SdkStarter;
import com.joffrey.iracing.irsdkjava.telemetry.model.TelemetryData;
import com.joffrey.iracing.irsdkjava.telemetry.model.TelemetryData.FuelAndAngles;
import com.joffrey.iracing.irsdkjava.telemetry.model.TelemetryData.PedalsAndSpeed;
import com.joffrey.iracing.irsdkjava.telemetry.model.TelemetryData.Session;
import com.joffrey.iracing.irsdkjava.telemetry.model.TelemetryData.Weather;

import java.time.Duration;
import java.util.Arrays;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log
@Service
public class TelemetryService {

    private final FluxProperties fluxProperties;
    private final SdkStarter sdkStarter;
    private final ConnectableFlux<TelemetryData> telemetryDataFlux;

    public TelemetryService(FluxProperties fluxProperties, SdkStarter sdkStarter) {
        this.fluxProperties = fluxProperties;
        this.sdkStarter = sdkStarter;
        this.telemetryDataFlux = Flux.interval(Duration.ofMillis(this.fluxProperties.getTelemetryIntervalInMs()))
                .filter(aLong -> sdkStarter.isRunning()).flatMap(aLong -> loadTelemetryData()).publish();

    }

    public Flux<TelemetryData> getTelemetryDataFlux() {
        return telemetryDataFlux.autoConnect();
    }

    private Flux<TelemetryData> loadTelemetryData() {

        final TelemetryData telemetryData = new TelemetryData();

        telemetryData.setPedalsAndSpeed(new PedalsAndSpeed(
                sdkStarter.getVarFloat("Throttle"),
                sdkStarter.getVarFloat("Brake"),
                sdkStarter.getVarFloat("Clutch"),
                sdkStarter.getVarInt("Gear"),
                sdkStarter.getVarFloat("ShiftGrindRPM"),
                sdkStarter.getVarFloat("RPM"),
                sdkStarter.getVarFloat("Speed")
        ));

        telemetryData.setFuelAndAngles(new FuelAndAngles(
                sdkStarter.getVarFloat("FuelLevel"),
                sdkStarter.getVarFloat("FuelLevelPct"),
                sdkStarter.getVarFloat("FuelUsePerHour"),
                sdkStarter.getVarFloat("LatAccel"),
                sdkStarter.getVarFloat("LongAccel"),
                sdkStarter.getVarFloat("SteeringWheelAngle")
        ));

        Arrays.stream(new String[]{"LF", "RF", "LR", "RR"}).forEach(tyre ->
                telemetryData.setTyre(tyre, new TelemetryData.Tyre(
                        sdkStarter.getVarFloat(tyre + "wearL"),
                        sdkStarter.getVarFloat(tyre + "wearM"),
                        sdkStarter.getVarFloat(tyre + "wearR"),
                        sdkStarter.getVarFloat(tyre + "tempL"),
                        sdkStarter.getVarFloat(tyre + "tempM"),
                        sdkStarter.getVarFloat(tyre + "tempR"),
                        sdkStarter.getVarFloat(tyre + "tempCL"),
                        sdkStarter.getVarFloat(tyre + "tempCM"),
                        sdkStarter.getVarFloat(tyre + "tempCR"),
                        sdkStarter.getVarFloat(tyre + "pressure"),
                        sdkStarter.getVarFloat(tyre + "speed")))
        );

        telemetryData.setWeather(new Weather(
                sdkStarter.getVarFloat("AirPressure"),
                sdkStarter.getVarFloat("AirTemp"),
                sdkStarter.getVarFloat("RelativeHumidity"),
                getSkies(sdkStarter.getVarInt("Skies")),
                sdkStarter.getVarFloat("TrackTemp"),
                sdkStarter.getVarFloat("WindDir"),
                sdkStarter.getVarFloat("WindVel"),
                getWeatherType(sdkStarter.getVarInt("WeatherType"))
        ));

        telemetryData.setSession(new Session(
                sdkStarter.getVarDouble("SessionTime"),
                sdkStarter.getVarDouble("SessionTimeRemain"),
                sdkStarter.getVarFloat("LapBestLapTime"),
                sdkStarter.getVarInt("Lap"),
                sdkStarter.getVarFloat("LapCurrentLapTime"),
                sdkStarter.getVarInt("LapBestLap"),
                sdkStarter.getVarFloat("LapDistPct")
        ));

        return Flux.just(telemetryData);

    }

    private String getWeatherType(Integer weatherIntVal) {
        if (weatherIntVal == 0) {
            return "Constant";
        } else if (weatherIntVal == 1) {
            return "Dynamic";
        } else {
            return "Unknown";
        }
    }

    private String getSkies(Integer skies) {
        if (skies == 0) {
            return "Clear";
        } else if (skies == 1 || skies == 2) {
            return "Cloudy";
        } else if (skies == 3) {
            return "Overcast";
        } else {
            return "Unknown";
        }
    }


}
