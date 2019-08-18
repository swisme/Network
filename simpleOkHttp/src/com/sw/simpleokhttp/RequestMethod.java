package com.sw.simpleokhttp;

public final class RequestMethod {
    public String name;

    private RequestMethod() {
    }

    private RequestMethod(String name) {
        this.name = name;
    }

    public static RequestMethod GET = new RequestMethod("GET");
    public static RequestMethod POST = new RequestMethod("POST");
}
