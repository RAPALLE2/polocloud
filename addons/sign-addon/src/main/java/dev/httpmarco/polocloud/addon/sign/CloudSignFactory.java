/*
 * Copyright 2024 Mirco Lindenau | HttpMarco
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.httpmarco.polocloud.addon.sign;

import dev.httpmarco.polocloud.api.CloudAPI;
import dev.httpmarco.polocloud.api.events.service.CloudServiceOnlineEvent;
import dev.httpmarco.polocloud.api.events.service.CloudServiceShutdownEvent;

public abstract class CloudSignFactory {

    public CloudSignFactory() {
        CloudAPI.instance().globalEventNode().addListener(CloudServiceOnlineEvent.class, event -> {
            for (var sign : CloudSignService.instance().signs()) {

                if (!sign.group().equalsIgnoreCase(event.cloudService().group().name())) {
                    continue;
                }

                if (sign.cloudService() == null && sign.state() == CloudSignState.SEARCHING) {
                    sign.append(event.cloudService());
                    break;
                }
            }
        });
        CloudAPI.instance().globalEventNode().addListener(CloudServiceShutdownEvent.class, event -> {
            for (var sign : CloudSignService.instance().signs()) {
                if (sign.cloudService() != null && sign.cloudService().name().equals(event.cloudService().name())) {
                    sign.remove();
                }
            }
        });
    }

    public abstract void pre(CloudSign sign);

    public abstract void print(CloudSign sign);

    public abstract void clear();

}