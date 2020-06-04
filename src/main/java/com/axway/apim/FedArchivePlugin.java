package com.axway.apim;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

@Mojo(name = "fed-archive", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true)
public class FedArchivePlugin extends AbstractMojo {

    @Parameter(property = "fedName", defaultValue = "default.fed")
    private String fedName;

    @Parameter(property = "fedDir", defaultValue = "default.fed")
    private File fedDir;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Log logger = getLog();
        logger.info("Deployment started....");
        App app = new App();
        app.run(fedName, fedDir);

    }
}
