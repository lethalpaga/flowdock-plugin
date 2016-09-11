import lib.LayoutTagLib
import lib.FormTagLib

l=namespace(LayoutTagLib)
t=namespace("/lib/hudson")
st=namespace("jelly:stapler")
f=namespace(FormTagLib)

f.section(title: "Connection settings") {
    f.entry(title:"Flow API token(s)", field:"flowToken") {
        f.textbox()
    }

    f.validateButton(title: "Test connection", progress: "Sending...", method: "testConnection", with: "flowToken, apiUrl, notificationTags")
}

f.entry(title: "Messages") {
    /*f.hetero_list(name: "messages",
                  //items: instance ? instance.messages : [],
                  hasHeader: "true",
                  descriptor: descriptor.flowdockMessageDescriptors,
                  targetType: com.flowdock.jenkins.FlowdockMessage.class)
                  */
}

f.section(title: "Advanced Options") {
    f.advanced() {
        f.entry(title: "Api Url", field: "apiUrl") {
            f.textbox(default: descriptor.defaultApiUrl)
        }
    }
}