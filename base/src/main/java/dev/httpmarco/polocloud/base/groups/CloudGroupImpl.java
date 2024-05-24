package dev.httpmarco.polocloud.base.groups;

import dev.httpmarco.polocloud.api.CloudAPI;
import dev.httpmarco.polocloud.api.groups.CloudGroup;
import dev.httpmarco.polocloud.api.groups.GroupProperties;
import dev.httpmarco.polocloud.api.properties.PropertiesPool;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
@AllArgsConstructor
public class CloudGroupImpl implements CloudGroup {

    private final String name;
    private final String platform;
    private final int memory;
    private final int minOnlineServices;
    private final PropertiesPool<GroupProperties<?>> properties;

    public CloudGroupImpl(String name, String platform, int memory, int minOnlineServices) {
        this.minOnlineServices = minOnlineServices;
        this.memory = memory;
        this.platform = platform;
        this.name = name;
        this.properties = new PropertiesPool<>();
    }

    @Override
    public int onlineAmount() {
        return CloudAPI.instance().serviceProvider().services(this).size();
    }

    @Override
    public String toString() {
        return "platform='" + platform + '\'' +
                ", memory=" + memory +
                ", minOnlineServices=" + minOnlineServices;
    }
}
