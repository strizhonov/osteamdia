package com.strizhonovapps.skinsearcher.osteamdia;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

@Component
@RequiredArgsConstructor
public class AppRunner implements CommandLineRunner, ExitCodeGenerator {

    private final RunCommand runCommand;

    private int exitCode;

    @Override
    public void run(String... args) {
        exitCode = new CommandLine(runCommand)
                .setCaseInsensitiveEnumValuesAllowed(true)
                .execute(args);
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}