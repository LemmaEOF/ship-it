package dev.emi.shipit.registry;

import dev.emi.shipit.component.LevelMailComponent;
import dev.emi.shipit.component.MailComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.level.LevelComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.level.LevelComponentInitializer;
import net.minecraft.util.Identifier;

public class ShipItComponents implements LevelComponentInitializer {
	public static final ComponentKey<MailComponent> MAIL = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("shipit", "mail"), MailComponent.class);

	@Override
	public void registerLevelComponentFactories(LevelComponentFactoryRegistry registry) {
		registry.register(MAIL, LevelMailComponent.class, LevelMailComponent::new);
	}

}
