package cl.magal.asistencia.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Attachment {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Long attachmentId;
	
	String name;	
	String description;
	String fileURL;
	
	public Long getAttachmentId() {
		return attachmentId;
	}
	public void setAttachmentId(Long attachmentId) {
		this.attachmentId = attachmentId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getFileURL() {
		return fileURL;
	}
	public void setFileURL(String fileURL) {
		this.fileURL = fileURL;
	}
	@Override
	public String toString() {
		return "Attachment [attachmentId=" + attachmentId + ", name=" + name
				+ ", description=" + description + ", fileURL=" + fileURL + "]";
	}
}
