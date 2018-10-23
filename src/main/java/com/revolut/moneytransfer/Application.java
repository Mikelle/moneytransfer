package com.revolut.moneytransfer;

import com.revolut.moneytransfer.route.Routes;

public class Application {

    public static void main(String[] args) {
        Routes routes = new Routes();
        routes.initRoutes();
    }
}
