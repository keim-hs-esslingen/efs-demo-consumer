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

import de.hsesslingen.keim.efs.middleware.consumer.ProviderProxy;
import de.hsesslingen.keim.efs.middleware.consumer.ServiceDirectoryProxy;
import de.hsesslingen.keim.efs.mobility.service.MobilityService;
import de.hsesslingen.keim.efs.mobility.service.MobilityType;
import de.hsesslingen.keim.efs.mobility.service.Mode;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * If you want to query the service directory directly, you can do so using the
 * ServiceDirectoryProxy.
 *
 * @author keim
 */
@Service
public class ServiceDirectoryProxyExample {

    @Autowired
    private ServiceDirectoryProxy serviceDirectory;

    public void queryServices() {
        // Get all available services currently in the service directory.
        var all = serviceDirectory.getAll();

        var filtered = serviceDirectory.search(Set.of(MobilityType.FREE_RIDE), Set.of(Mode.BICYCLE), MobilityService.API.OPTIONS_API);

        // Having a list of mobility services you can easily create a ProviderProxy
        // from each one of them to query those providers
        filtered.stream()
                .map(s -> new ProviderProxy(s))
                .forEach(proxy -> {
                    // Use the proxy object to query the provider.
                });

    }

}
