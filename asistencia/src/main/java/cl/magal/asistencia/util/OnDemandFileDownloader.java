package cl.magal.asistencia.util;

import java.io.IOException;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;

/**
 * Clase copiada talcual de internet 
 * http://stackoverflow.com/questions/15815848/lazily-detemine-filename-when-using-filedownloader
 * This specializes {@link FileDownloader} in a way, such that both the file name and content can be determined
 * on-demand, i.e. when the user has clicked the component.
 */
public class OnDemandFileDownloader extends FileDownloader {

	/**
	   * Provide both the {@link StreamSource} and the filename in an on-demand way.
	   */
	  public interface OnDemandStreamResource extends StreamSource {
	    String getFilename ();
	  }

	  private static final long serialVersionUID = 1L;
	  private final OnDemandStreamResource onDemandStreamResource;

	  public OnDemandFileDownloader (OnDemandStreamResource onDemandStreamResource) {
	    super(new StreamResource(onDemandStreamResource, ""));
	    this.onDemandStreamResource = checkNotNull(onDemandStreamResource,
	      "The given on-demand stream resource may never be null!");
	  }

	  private OnDemandStreamResource checkNotNull(OnDemandStreamResource onDemandStreamResource2, String string) {
		  if(onDemandStreamResource2 == null )
			  throw new RuntimeException(string);
		return onDemandStreamResource2;
	}

	@Override
	  public boolean handleConnectorRequest (VaadinRequest request, VaadinResponse response, String path)
	      throws IOException {
	    getResource().setFilename(onDemandStreamResource.getFilename());
	    return super.handleConnectorRequest(request, response, path);
	  }

	  private StreamResource getResource () {
	    return (StreamResource) this.getResource("dl");
	  }
}
