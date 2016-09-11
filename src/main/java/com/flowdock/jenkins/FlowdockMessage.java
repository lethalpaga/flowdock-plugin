package com.flowdock.jenkins;

import com.flowdock.jenkins.exception.FlowdockException;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public abstract class FlowdockMessage extends AbstractDescribableImpl<FlowdockMessage> {
    protected String content;
    protected String tags;
    protected String threadId;

    @DataBoundSetter
    public void setContent(String content) {
        this.content = content;
    }

    @DataBoundSetter
    public void setTags(String tags) {
        this.tags = tags;
    }

    @DataBoundSetter
    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public abstract void send(FlowdockAPI api) throws FlowdockException;

    public abstract String asPostData() throws UnsupportedEncodingException;

    protected String removeWhitespace(String data) {
        return data == null ? null : tags.replaceAll("\\s", "");
    }

    protected String urlEncode(String data) throws UnsupportedEncodingException {
        return data == null ? "" : URLEncoder.encode(data, "UTF-8");
    }
}

