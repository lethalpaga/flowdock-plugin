package com.flowdock.jenkins;

import com.flowdock.jenkins.exception.FlowdockException;
import com.google.inject.Inject;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FlowdockBuildStep extends Builder implements Describable<Builder>, SimpleBuildStep {
    // API Url
    private String apiUrl;

    public String getApiUrl() {
        return this.apiUrl;
    }

    @DataBoundSetter
    public void setApiUrl(String value) {
        this.apiUrl = value;
    }

    // Flow token
    private String flowToken;

    public String getFlowToken() {
        return this.flowToken;
    }

    @DataBoundSetter
    public void setFlowToken(String value) {
        this.flowToken = value;
    }

    // Message
    private List<FlowdockMessage> messages;

    List<FlowdockMessage> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    void setMessages(List<FlowdockMessage> value) {
        this.messages = value;
    }

    // Fields should all be constructed using the DataBoundSetters
    @DataBoundConstructor
    public FlowdockBuildStep(List<FlowdockMessage> messages, String apiUrl) {
        this.apiUrl = apiUrl == null ? getDescriptor().getDefaultApiUrl() : apiUrl;
        this.messages = messages == null ? new ArrayList<FlowdockMessage>() : new ArrayList<FlowdockMessage>(messages);
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
        try {
            FlowdockAPI api = new FlowdockAPI(getApiUrl(), flowToken);
            for (FlowdockMessage message : messages) {
                message.send(api);
            }
            listener.getLogger().println("Flowdock: " + messages.size() + " message(s) sent successfully");
        }
        catch(FlowdockException ex) {
            listener.getLogger().println("Flowdock: failed to send notification");
            listener.getLogger().println("Flowdock: " + ex.getMessage());
        }
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        public DescriptorImpl() {
            load();
        }

        public String getDisplayName() {
            return "Flowdock message";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            apiUrl = GlobalConfiguration.all().get(FlowdockConfig.class).getApiUrl();
            return super.configure(req, json);
        }

        public FormValidation doTestConnection(@QueryParameter("flowToken") final String flowToken,
                                               @QueryParameter("apiUrl") final String apiUrl) {
            try {
                FlowdockAPI api = new FlowdockAPI(apiUrl, flowToken);
                TeamInboxMessage testMsg = new TeamInboxMessage();
                testMsg.setTags("Test");
                testMsg.setContent("Your plugin is ready!");
                testMsg.setSubject("Testing jenkins plugin");
                api.pushTeamInboxMessage(testMsg);
                return FormValidation.ok("Success! Flowdock plugin can send notifications to your flow.");
            } catch(FlowdockException ex) {
                return FormValidation.error(ex.getMessage());
            }
        }

        private String apiUrl;

        public String getApiUrl() {
            return this.apiUrl;
        }

        public void setApiUrl(String value) {
            this.apiUrl = value;
        }

        public String getDefaultApiUrl() {
            return GlobalConfiguration.all().get(FlowdockConfig.class).getApiUrl();
        }

        public List<FlowdockMessageDescriptor> getFlowdockMessageDescriptors() {
            return Jenkins.getInstance().getDescriptorList(FlowdockMessage.class);
        }
    }
}
