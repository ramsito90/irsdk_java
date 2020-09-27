/*
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
 */

package com.joffrey.irsdkjava.library;

import com.joffrey.irsdkjava.SdkStarter;
import com.joffrey.irsdkjava.library.info.InfoDataService;
import com.joffrey.irsdkjava.library.laptiming.LapTimingService;
import com.joffrey.irsdkjava.library.laptiming.model.LapTimingData;
import com.joffrey.irsdkjava.library.info.model.RaceInfo;
import com.joffrey.irsdkjava.library.telemetry.model.TelemetryData;
import com.joffrey.irsdkjava.library.telemetry.service.TelemetryService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class IRacingLibrary implements CommandLineRunner {

    private final SdkStarter       sdkStarter;
    private final LapTimingService lapTimingService;
    private final InfoDataService  infoDataService;
    private final TelemetryService telemetryService;

    // @Override
    // public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
    //     while (sdkStarter.isRunning()) {
    //         infoDataService.loadRaceInfo();
    //         lapTimingDataService.loadLapTimingDataList();
    //     }
    // }

    public Map<Integer, LapTimingData> getLapTimingDataList() {
        return lapTimingService.getLapTimingDataList();
    }

    public RaceInfo getRaceInfo() {
        return infoDataService.getRaceInfo();
    }

    public TelemetryData getTelemetryData() {
        return telemetryService.getTelemetryData();
    }

    @Override
    public void run(String... args) throws Exception {
        while (true) {
            while (sdkStarter.isRunning()) {
                // infoDataService.loadRaceInfo();
                // lapTimingService.loadLapTimingDataList();
                telemetryService.loadTelemetryData();
            }
        }
    }
}
