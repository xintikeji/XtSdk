package com.xt.library.beans;

public class ElevatorStatusInfo {

    /**
     * deviceToken : MPUV30002018120200610
     * type : DeviceAlert
     * request : {"level":"Information","type":"currentMeasurement","message":"设备测量信息","updateState":false,"eventDate":"2020-02-19T04:07:29.972Z","metadata":{"floor":"27","direction":"1","noise_value":"70.000000","level_signal":"0.000000","safety_loop":"6.271204","door_lock_loop":"0.003634","acceleration_z":"0.012762","acceleration_x":"0.007165","acceleration_y":"0.003011","left_brake":"0.000000","right_brake":"0.000000"}}
     */

    private String deviceToken;
    private String type;
    private RequestBean request;

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public RequestBean getRequest() {
        return request;
    }

    public void setRequest(RequestBean request) {
        this.request = request;
    }

    public static class RequestBean {
        /**
         * level : Information
         * type : currentMeasurement
         * message : 设备测量信息
         * updateState : false
         * eventDate : 2020-02-19T04:07:29.972Z
         * metadata : {"floor":"27","direction":"1","noise_value":"70.000000","level_signal":"0.000000","safety_loop":"6.271204","door_lock_loop":"0.003634","acceleration_z":"0.012762","acceleration_x":"0.007165","acceleration_y":"0.003011","left_brake":"0.000000","right_brake":"0.000000"}
         */

        private String level;
        private String type;
        private String message;
        private boolean updateState;
        private String eventDate;
        private MetadataBean metadata;

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public boolean isUpdateState() {
            return updateState;
        }

        public void setUpdateState(boolean updateState) {
            this.updateState = updateState;
        }

        public String getEventDate() {
            return eventDate;
        }

        public void setEventDate(String eventDate) {
            this.eventDate = eventDate;
        }

        public MetadataBean getMetadata() {
            return metadata;
        }

        public void setMetadata(MetadataBean metadata) {
            this.metadata = metadata;
        }

        public static class MetadataBean {
            /**
             * floor : 27
             * direction : 1
             * noise_value : 70.000000
             * level_signal : 0.000000
             * safety_loop : 6.271204
             * door_lock_loop : 0.003634
             * acceleration_z : 0.012762
             * acceleration_x : 0.007165
             * acceleration_y : 0.003011
             * left_brake : 0.000000
             * right_brake : 0.000000
             */

            private String floor;
            private String direction;
            private String noise_value;
            private String level_signal;
            private String safety_loop;
            private String door_lock_loop;
            private String acceleration_z;
            private String acceleration_x;
            private String acceleration_y;
            private String left_brake;
            private String right_brake;

            public String getFloor() {
                return floor;
            }

            public void setFloor(String floor) {
                this.floor = floor;
            }

            public String getDirection() {
                return direction;
            }

            public void setDirection(String direction) {
                this.direction = direction;
            }

            public String getNoise_value() {
                return noise_value;
            }

            public void setNoise_value(String noise_value) {
                this.noise_value = noise_value;
            }

            public String getLevel_signal() {
                return level_signal;
            }

            public void setLevel_signal(String level_signal) {
                this.level_signal = level_signal;
            }

            public String getSafety_loop() {
                return safety_loop;
            }

            public void setSafety_loop(String safety_loop) {
                this.safety_loop = safety_loop;
            }

            public String getDoor_lock_loop() {
                return door_lock_loop;
            }

            public void setDoor_lock_loop(String door_lock_loop) {
                this.door_lock_loop = door_lock_loop;
            }

            public String getAcceleration_z() {
                return acceleration_z;
            }

            public void setAcceleration_z(String acceleration_z) {
                this.acceleration_z = acceleration_z;
            }

            public String getAcceleration_x() {
                return acceleration_x;
            }

            public void setAcceleration_x(String acceleration_x) {
                this.acceleration_x = acceleration_x;
            }

            public String getAcceleration_y() {
                return acceleration_y;
            }

            public void setAcceleration_y(String acceleration_y) {
                this.acceleration_y = acceleration_y;
            }

            public String getLeft_brake() {
                return left_brake;
            }

            public void setLeft_brake(String left_brake) {
                this.left_brake = left_brake;
            }

            public String getRight_brake() {
                return right_brake;
            }

            public void setRight_brake(String right_brake) {
                this.right_brake = right_brake;
            }

            @Override
            public String toString() {
                return "MetadataBean{" +
                        "floor='" + floor + '\'' +
                        ", direction='" + direction + '\'' +
                        ", noise_value='" + noise_value + '\'' +
                        ", level_signal='" + level_signal + '\'' +
                        ", safety_loop='" + safety_loop + '\'' +
                        ", door_lock_loop='" + door_lock_loop + '\'' +
                        ", acceleration_z='" + acceleration_z + '\'' +
                        ", acceleration_x='" + acceleration_x + '\'' +
                        ", acceleration_y='" + acceleration_y + '\'' +
                        ", left_brake='" + left_brake + '\'' +
                        ", right_brake='" + right_brake + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "RequestBean{" +
                    "level='" + level + '\'' +
                    ", type='" + type + '\'' +
                    ", message='" + message + '\'' +
                    ", updateState=" + updateState +
                    ", eventDate='" + eventDate + '\'' +
                    ", metadata=" + metadata +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ElevatorStatusInfo{" +
                "deviceToken='" + deviceToken + '\'' +
                ", type='" + type + '\'' +
                ", request=" + request +
                '}';
    }
}
