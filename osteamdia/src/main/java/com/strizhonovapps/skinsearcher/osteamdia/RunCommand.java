package com.strizhonovapps.skinsearcher.osteamdia;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.util.List;
import java.util.concurrent.Callable;

@Component
@RequiredArgsConstructor
@CommandLine.Command(name = "oi", mixinStandardHelpOptions = true)
public class RunCommand implements Callable<Integer> {

    @CommandLine.Option(names = "--app.secure.steam", description = "Steam secure cookie", required = true)
    String steamLoginSecureCookie;

    @CommandLine.Option(names = "--app.sequentialHistory", description = "Do trade history have each year data")
    Boolean sequentialHistory;

    private final List<Runnable> runnables;

    @Override
    public Integer call() {
        runnables.forEach(Runnable::run);
        return 0;
    }


}