package com.flowdock.jenkins;

import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Handles the global configuration
 * Access in extension points with GlobalConfiguration.all().get(FlowdockConfig.class)
 */
@Extension
public class FlowdockConfig  extends GlobalConfiguration {
    private String apiUrl;

    public FlowdockConfig() {
        load();
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String value) {
        apiUrl = value;
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
        apiUrl = formData.getString("apiUrl");
        save();
        return super.configure(req, formData);
    }
}
