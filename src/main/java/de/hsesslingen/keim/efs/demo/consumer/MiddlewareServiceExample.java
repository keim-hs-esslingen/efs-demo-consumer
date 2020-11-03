/*
 * MIT License
 * 
 * Copyright (c) 2020 Hochschule Esslingen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE. 
 */
package de.hsesslingen.keim.efs.demo.consumer;

import de.hsesslingen.keim.efs.middleware.consumer.MiddlewareService;
import de.hsesslingen.keim.efs.middleware.model.ICoordinates;
import de.hsesslingen.keim.efs.mobility.service.MobilityService;
import de.hsesslingen.keim.efs.mobility.service.MobilityType;
import de.hsesslingen.keim.efs.mobility.service.Mode;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import static java.util.stream.Collectors.toList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This is an example class on how to utilize the MiddlewareService Spring bean.
 *
 * @author keim
 */
@Service
public class MiddlewareServiceExample {

    @Autowired
    private MiddlewareService middleware;

    // This function is a placeholder for a method to retrieve tokens for specific services from your database.
    private Function<String, String> serviceTokenGetter;

    /**
     * Use getProviders() to get a list of all available providers, each one
     * represented by a ProviderProxy. A ProviderProxy is an object that
     * simplifies querying the APIs of a provider.
     */
    public void getProvidersAndQueryThem() {
        var providerProxies = middleware.getProviders();

        providerProxies
                .stream()
                .forEach(proxy -> {
                    // Do whatever you like using the proxy.
                });

        // You can also get a filtered stream of ProviderProxys:
        var modes = Set.of(Mode.BICYCLE, Mode.CAR); // Only providers that support at least on of the given modes are returned.
        var mobilityTypes = Set.of(MobilityType.FREE_RIDE); // Only providers that support at least on of the given mobility types are returned.
        var apis = Set.of(MobilityService.API.OPTIONS_API); // Only providers that support ALL of the given APIs are returned.

        var filteredStream = middleware.getProviders(modes, mobilityTypes, apis);

        filteredStream.forEach(proxy -> {
            // Do whatever you like using the proxy.
        });

        // You can also query the MiddlewareService to only return providers with particular service ids.
        var filteredByServiceId = middleware.getProviders(Set.of("service-a", "service-b"));

        filteredByServiceId.forEach(proxy -> {
            // Do whatever you like using the proxy.
        });

        // If you are interested in one particular provider, you can get it using getProvider()
        var proxy = middleware.getProvider("service-a");
        // If no such proxy is there, the returned object will be null.
    }

    /**
     * The MiddlewareService also providers functions that address multiple
     * providers at once.
     */
    public void queryOptionsOfBookingsFromMatchingProviders(
            ICoordinates from,
            ICoordinates to,
            ZonedDateTime startTime,
            ZonedDateTime endTime,
            Integer radiusMeter,
            Boolean sharingAllowed,
            Set<Mode> modesAllowed,
            Set<MobilityType> mobilityTypesAllowed,
            Integer limitTo
    ) {
        // Use one of the getOptions methods to rerieve options from multiple providers.
        // The options are returned as a stream, not a list, so they can be efficiently 
        // processed afterwards. This makes sense because not all providers respond in 
        // the same speed and sometimes they take a while to respond.

        var optionsStream = middleware.getOptions(
                from, to, startTime, endTime, radiusMeter, sharingAllowed,
                modesAllowed, mobilityTypesAllowed, limitTo, Map.of()
        );

        // Short-circuit by limiting to a given number of options in total:
        optionsStream = optionsStream.limit(5);

        // Short-circuit by using a dynamic condition:
        AtomicBoolean condition = new AtomicBoolean(true);
        optionsStream = optionsStream.takeWhile(o -> condition.get());
        // Set condition to false if you no longer want more options.
        // This can be done at another place as well.
        condition.set(false);

        // Collect items. This will start sending the actual requests to the providers.
        var options = optionsStream.collect(toList());

        // You can also use the getBooking method to retrieve bookings from multiple providers at once.
        var bookings = middleware.getBookings(Set.of("service-a"), serviceTokenGetter);
    }

}
