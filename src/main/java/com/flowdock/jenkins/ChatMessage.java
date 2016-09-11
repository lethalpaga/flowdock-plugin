package com.flowdock.jenkins;

import com.flowdock.jenkins.exception.FlowdockException;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Hudson;
import hudson.model.Result;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.UnsupportedEncodingException;

public class ChatMessage extends FlowdockMessage {
    protected String externalUserName;

    @DataBoundConstructor
    public ChatMessage() {
        this.externalUserName = "Jenkins";
    }

    @Override
    public void send(FlowdockAPI api) throws FlowdockException {
        api.pushChatMessage(this);
    }

    @DataBoundSetter
    public void setExternalUserName(String externalUserName) {
        this.externalUserName = externalUserName;
    }

    public String asPostData() throws UnsupportedEncodingException {
        StringBuilder postData = new StringBuilder();
        postData.append("content=").append(urlEncode(content));
        postData.append("&external_user_name=").append(urlEncode(externalUserName));
        postData.append("&tags=").append(urlEncode(removeWhitespace(tags)));
        return postData.toString();
    }

    public static ChatMessage fromBuild(AbstractBuild build, BuildResult buildResult, BuildListener listener) {
        ChatMessage msg = new ChatMessage();
        StringBuilder content = new StringBuilder();

        String projectName = "";
        String configuration = "";
        if(build.getProject().getRootProject() != build.getProject()) {
            projectName = build.getProject().getRootProject().getDisplayName();
            configuration = " on " + build.getProject().getDisplayName();
        } else {
            projectName = build.getProject().getDisplayName();
        }

        String rootUrl = Hudson.getInstance().getRootUrl();
        String buildLink = (rootUrl == null) ? null : rootUrl + build.getUrl();
        boolean hasLink = buildLink != null;
        String buildNo = build.getDisplayName().replaceAll("#", "");

        if(build.getResult() == Result.SUCCESS) {
            content.append(":white_check_mark:");
        }
        else if(build.getResult() == Result.UNSTABLE) {
            content.append(":heavy_exclamation_mark:");
        }
        else if(build.getResult() == Result.FAILURE) {
            content.append(":x:");
        }
        else if(build.getResult() == Result.ABORTED) {
            content.append(":no_entry_sign:");
        }
        else if(build.getResult() == Result.NOT_BUILT) {
            content.append(":o:");
        }
        if(hasLink) {
            content.append("[");
        }
        content.append(projectName + configuration).append(" build ").append(buildNo);
        content.append(" **").append(buildResult.getHumanResult()).append("**");
        if(hasLink) {
            content.append("]");
            content.append("(" + buildLink + ")");
        }

        msg.setContent(content.toString());
        return msg;
    }

    @Extension
    public static class DescriptorImpl extends FlowdockMessageDescriptor {
        public String getDisplayName() {
            return "Chat message";
        }
    }
}
