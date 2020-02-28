package com.xt.library.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jlp on2019/8/8 .
 */
public class MqttCommandBean implements Serializable {

    /**
     * command : {"command":{"namespace":"sip_rtsp","name":"sip_rtsp","description":"通话及推流","parameters":[]},"invocation":{"parameterValues":{"sip":"0","dateTime":"1555320490878","Sipcontact":[],"Sipdelay":"60","eventid":"178981-1201-121-121121"}},"parameters":{}}
     * nestingContext : {"gateway":{"token":"D11254957"}}
     * assignment : {}
     */

    private CommandBeanX command;
    private NestingContextBean nestingContext;
    private AssignmentBean assignment;

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

    public AssignmentBean getAssignment() {
        return assignment;
    }

    public void setAssignment(AssignmentBean assignment) {
        this.assignment = assignment;
    }

    public static class CommandBeanX implements Serializable {
        /**
         * command : {"namespace":"sip_rtsp","name":"sip_rtsp","description":"通话及推流","parameters":[]}
         * invocation : {"parameterValues":{"sip":"0","dateTime":"1555320490878","Sipcontact":[],"Sipdelay":"60","eventid":"178981-1201-121-121121"}}
         * parameters : {}
         */

        private CommandBean command;
        private InvocationBean invocation;
        private ParametersBean parameters;

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

        public ParametersBean getParameters() {
            return parameters;
        }

        public void setParameters(ParametersBean parameters) {
            this.parameters = parameters;
        }

        public static class CommandBean implements Serializable {
            /**
             * namespace : sip_rtsp
             * name : sip_rtsp
             * description : 通话及推流
             * parameters : []
             */

            private String namespace;
            private String name;
            private String description;
            private List<?> parameters;

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

            public List<?> getParameters() {
                return parameters;
            }

            public void setParameters(List<?> parameters) {
                this.parameters = parameters;
            }
        }

        public static class InvocationBean implements Serializable {
            /**
             * parameterValues : {"sip":"0","dateTime":"1555320490878","Sipcontact":[],"Sipdelay":"60","eventid":"178981-1201-121-121121"}
             */

            private ParameterValuesBean parameterValues;

            public ParameterValuesBean getParameterValues() {
                return parameterValues;
            }

            public void setParameterValues(ParameterValuesBean parameterValues) {
                this.parameterValues = parameterValues;
            }

            public static class ParameterValuesBean implements Serializable {
                /**
                 * sip : 0
                 * dateTime : 1555320490878
                 * Sipcontact : []
                 * Sipdelay : 60
                 * eventid : 178981-1201-121-121121
                 */

                private String sip;
                private String dateTime;
                private String Sipdelay;
                private String eventid;
                private List<?> Sipcontact;

                public String getSip() {
                    return sip;
                }

                public void setSip(String sip) {
                    this.sip = sip;
                }

                public String getDateTime() {
                    return dateTime;
                }

                public void setDateTime(String dateTime) {
                    this.dateTime = dateTime;
                }

                public String getSipdelay() {
                    return Sipdelay;
                }

                public void setSipdelay(String Sipdelay) {
                    this.Sipdelay = Sipdelay;
                }

                public String getEventid() {
                    return eventid;
                }

                public void setEventid(String eventid) {
                    this.eventid = eventid;
                }

                public List<?> getSipcontact() {
                    return Sipcontact;
                }

                public void setSipcontact(List<?> Sipcontact) {
                    this.Sipcontact = Sipcontact;
                }
            }
        }

        public static class ParametersBean implements Serializable {
        }
    }

    public static class NestingContextBean implements Serializable {
        /**
         * gateway : {"token":"D11254957"}
         */

        private GatewayBean gateway;

        public GatewayBean getGateway() {
            return gateway;
        }

        public void setGateway(GatewayBean gateway) {
            this.gateway = gateway;
        }

        public static class GatewayBean implements Serializable {
            /**
             * token : D11254957
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

    public static class AssignmentBean implements Serializable {
    }
}
