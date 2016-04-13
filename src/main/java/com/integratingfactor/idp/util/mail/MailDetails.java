package com.integratingfactor.idp.util.mail;

public class MailDetails {

    private Address to;

    private Address from;

    private String sub;

    private String msg;

    public static class Address {

        private String address;

        private String name;

        public Address() {
            // default constructor
        }

        public Address(String address, String name) {
            this.address = address;
            this.name = name;
        }
        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public Address getTo() {
        return to;
    }

    public void setTo(Address to) {
        this.to = to;
    }

    public Address getFrom() {
        return from;
    }

    public void setFrom(Address from) {
        this.from = from;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    };
}
