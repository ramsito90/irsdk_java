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

package com.joffrey.iracing.irsdkjava.telemetry.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class TelemetryData {

    // Pedals && Speed
    private PedalsAndSpeed pedalsAndSpeed = new PedalsAndSpeed();

    // Fuel && Angles
    private FuelAndAngles fuelAndAngles = new FuelAndAngles();

    // Tyres
    private Tyre LFtyre = new Tyre();
    private Tyre RFtyre = new Tyre();
    private Tyre LRtyre = new Tyre();
    private Tyre RRtyre = new Tyre();

    // Weather
    private Weather weather = new Weather();

    // Session
    private Session session = new Session();

    public void setTyre(String tyre, Tyre data) {
        switch (tyre) {
            case "LF":
                setLFtyre(data);
                break;
            case "RF":
                setRFtyre(data);
                break;
            case "LR":
                setLRtyre(data);
                break;
            case "RR":
                setRRtyre(data);
                break;
        }
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class PedalsAndSpeed {
        private float throttle;
        private float brake;
        private float clutch;
        private int   gear;
        private float shiftGrindRPM;
        private float RPM;
        private float speed;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class FuelAndAngles {
        private float fuelLevel;
        private float fuelLevelPct;
        private float fuelUsePerHour;
        private float latAccel;
        private float longAccel;
        private float steeringWheelAngle;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class Tyre {
        private float wearL;
        private float wearM;
        private float wearR;
        private float tempL;
        private float tempM;
        private float tempR;
        private float tempCL;
        private float tempCM;
        private float tempCR;
        private float pressure;
        private float speed;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class Weather {
        private float  airPressure;
        private float  airTemp;
        private float  relativeHumidity;
        private String skies;
        private float  trackTemp;
        private float  windDir;
        private float  windVel;
        private String weatherType;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class Session {
        private double sessionTime;
        private double sessionTimeRemain;
        private float  lapBestLapTime;
        private int    lap;
        private float  lapCurrentLapTime;
        private int    lapBestLap;
        private float  lapDistPct;
    }


}
