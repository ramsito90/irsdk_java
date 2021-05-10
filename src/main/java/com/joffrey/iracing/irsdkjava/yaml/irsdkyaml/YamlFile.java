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

package com.joffrey.iracing.irsdkjava.yaml.irsdkyaml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YamlFile {

    @JsonProperty
    private WeekendInfoYaml        WeekendInfo;
    @JsonProperty
    private SessionInfoYaml        SessionInfo;
    @JsonProperty
    private QualifyResultsInfoYaml QualifyResultsInfo;
    @JsonProperty
    private CamerasInfoYaml        CameraInfo;
    @JsonProperty
    private RadiosInfoYaml         RadioInfo;
    @JsonProperty
    private DriversInfoYaml        DriverInfo;
    @JsonProperty
    private SplitTimeInfoYaml      SplitTimeInfo;

    public static YamlFile initEmpty() {
        YamlFile yamlFile = new YamlFile();
        yamlFile.setWeekendInfo(WeekendInfoYaml.initEmpty());
        yamlFile.setSessionInfo(SessionInfoYaml.initEmpty());
        yamlFile.setQualifyResultsInfo(QualifyResultsInfoYaml.initEmpty());
        yamlFile.setCameraInfo(CamerasInfoYaml.initEmpty());
        yamlFile.setRadioInfo(RadiosInfoYaml.initEmpty());
        yamlFile.setDriverInfo(DriversInfoYaml.initEmpty());
        yamlFile.setSplitTimeInfo(SplitTimeInfoYaml.initEmpty());
        return yamlFile;
    }
}
