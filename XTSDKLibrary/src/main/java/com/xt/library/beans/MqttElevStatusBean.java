package com.xt.library.beans;

import java.util.List;

public class MqttElevStatusBean {


    /**
     * command : {"command":{"namespace":"currentMeasurement","name":"currentMeasurement","description":"电梯当前测量值上报","parameters":[{"name":"topic","type":"String","required":false}]},"invocation":{"parameterValues":{"topic":"zhongti/weibao/15321135351"}},"parameters":{"topic":"zhongti/weibao/15321135351"}}
     * nestingContext : {"gateway":{"token":"MPU352522111133333333"}}
     */

    private CommandBeanX command;
    private NestingContextBean nestingContext;

    public CommandBeanX getCommand() {
        return command;
    }

    public void setCommand(CommandBeanX command) {
        this.command = command;
    }

    public NestingContextBean getNestingContext() {
        return nestingContext;
    }

    public void setNestingContext(NestingContextBean nestingContext) {
        this.nestingContext = nestingContext;
    }

    public static class CommandBeanX {
        /**
         * command : {"namespace":"currentMeasurement","name":"currentMeasurement","description":"电梯当前测量值上报","parameters":[{"name":"topic","type":"String","required":false}]}
         * invocation : {"parameterValues":{"topic":"zhongti/weibao/15321135351"}}
         * parameters : {"topic":"zhongti/weibao/15321135351"}
         */

        private CommandBean command;
        private InvocationBean invocation;
        private ParametersBeanX parameters;

        public CommandBean getCommand() {
            return command;
        }

        public void setCommand(CommandBean command) {
            this.command = command;
        }

        public InvocationBean getInvocation() {
            return invocation;
        }

        public void setInvocation(InvocationBean invocation) {
            this.invocation = invocation;
        }

        public ParametersBeanX getParameters() {
            return parameters;
        }

        public void setParameters(ParametersBeanX parameters) {
            this.parameters = parameters;
        }

        public static class CommandBean {
            /**
             * namespace : currentMeasurement
             * name : currentMeasurement
             * description : 电梯当前测量值上报
             * parameters : [{"name":"topic","type":"String","required":false}]
             */

            private String namespace;
            private String name;
            private String description;
            private List<ParametersBean> parameters;

            public String getNamespace() {
                return namespace;
            }

            public void setNamespace(String namespace) {
                this.namespace = namespace;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public List<ParametersBean> getParameters() {
                return parameters;
            }

            public void setParameters(List<ParametersBean> parameters) {
                this.parameters = parameters;
            }

            public static class ParametersBean {
                /**
                 * name : topic
                 * type : String
                 * required : false
                 */

                private String name;
                private String type;
                private boolean required;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public boolean isRequired() {
                    return required;
                }

                public void setRequired(boolean required) {
                    this.required = required;
                }
            }
        }

        public static class InvocationBean {
            /**
             * parameterValues : {"topic":"zhongti/weibao/15321135351"}
             */

            private ParameterValuesBean parameterValues;

            public ParameterValuesBean getParameterValues() {
                return parameterValues;
            }

            public void setParameterValues(ParameterValuesBean parameterValues) {
                this.parameterValues = parameterValues;
            }

            public static class ParameterValuesBean {
                /**
                 * topic : zhongti/weibao/15321135351
                 */

                private String topic;

                public String getTopic() {
                    return topic;
                }

                public void setTopic(String topic) {
                    this.topic = topic;
                }
            }
        }

        public static class ParametersBeanX {
            /**
             * topic : zhongti/weibao/15321135351
             */

            private String topic;

            public String getTopic() {
                return topic;
            }

            public void setTopic(String topic) {
                this.topic = topic;
            }
        }
    }

    public static class NestingContextBean {
        /**
         * gateway : {"token":"MPU352522111133333333"}
         */

        private GatewayBean gateway;

        public GatewayBean getGateway() {
            return gateway;
        }

        public void setGateway(GatewayBean gateway) {
            this.gateway = gateway;
        }

        public static class GatewayBean {
            /**
             * token : MPU352522111133333333
             */

            private String token;

            public String getToken() {
                return token;
            }

            public void setToken(String token) {
                this.token = token;
            }
        }
    }
}
