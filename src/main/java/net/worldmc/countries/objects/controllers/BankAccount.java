package net.worldmc.countries.objects.controllers;

import net.worldmc.countries.Countries;
import net.worldmc.countries.economy.EconomyManager;

import java.util.UUID;

public class BankAccount {
    private UUID uuid;
    private final Countries countries = Countries.getInstance();

    public BankAccount(UUID accountUUID, String name) {
        this.uuid = accountUUID;
        initializeAccount(name);
    }

    public void setName(String name) {
        EconomyManager.getEconomy().renameAccount(uuid, name);
    }

    private void initializeAccount(String name) {
        if (!EconomyManager.getEconomy().hasAccount(uuid)) {
            String formattedName = name.replace(" ", "_");

            boolean response = EconomyManager.getEconomy().createAccount(uuid, "countries-" + formattedName);
            if (!response) {
                System.out.println("Failed to create account: " + name + " (" + uuid + ")");
            }
        }
    }

    public int getBalance() {
        return EconomyManager.getEconomy().getBalance(countries.getName(), uuid).intValue();
    }
}
