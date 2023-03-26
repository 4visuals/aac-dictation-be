package github.visual4.aacweb.dictation.service.mailing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import github.visual4.aacweb.dictation.AppException;
import github.visual4.aacweb.dictation.ErrorCode;

public class Mail {

    private EndPoint sender;
    
    private List<EndPoint> receivers;
    
    private String title;
    private String body;
    
    private List<Attachment> attachments;

    public Mail(String senderEmail) {
        this.sender = new EndPoint(senderEmail, senderEmail);
        this.attachments = new ArrayList<>();
        this.receivers = new ArrayList<>();
    }
    public Mail(String senderEmail, String receiverEmail,String title, String body) {
        this(senderEmail, senderEmail, receiverEmail, receiverEmail, title, body, new ArrayList<>());
    }
    public Mail(
            String senderEmail, String senderName,
            String receiverEmail,String receiverName,
            String title, String body, List<Attachment> attachments) {
        super();
        this.title = title;
        this.body = body;
        this.attachments = attachments;
        this.sender = new EndPoint(senderEmail, senderName);
        this.receivers = new ArrayList<>();
        addReceiver(receiverEmail, receiverName);
    }
    public void addReceiver(String email, String name) {
        this.receivers.add(new EndPoint(email, name));
    }
    public String getSenderEmail() {
        return sender.mail;
    }
    public String getSenderName() {
        return sender.name;
    }
    public void setSenderName(String senderName) {
        this.sender = sender.name(senderName);
    }
    public void setSenderEmail(String senderEmail) {
        this.sender = sender.email(senderEmail);
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public List<Attachment> getAttachments() {
        return attachments;
    }
    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }
    public void forEachReceivers(Consumer<EndPoint> callback) {
        for (EndPoint receiver : receivers) {
            callback.accept(receiver);
        }
    }
    public Mail addAttachment(File file, String contentType) {
        FileInputStream in;
        try {
            in = new FileInputStream(file);
            Attachment att = new Attachment(in, file.getName(), contentType);
            return this.addAttachment(att);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new AppException(ErrorCode.SERVER_ERROR, 500, "FILE_NOT_FOUND");
        }
    }
    public Mail addAttachment(Attachment attachment) {
        attachments.add(attachment);
        return this;
    }
    public static class EndPoint {
        final public String mail;
        final public String name;
        public EndPoint(String mail, String name) {
            super();
            this.mail = mail;
            this.name = name;
        }
        public EndPoint name(String senderName) {
            return new EndPoint(mail, senderName);
        }
        public EndPoint email(String email) {
            return new EndPoint(email, name);
        }
    }

}
