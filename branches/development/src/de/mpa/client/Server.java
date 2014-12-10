
package de.mpa.client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebService(name = "Server", targetNamespace = "http://webservice.mpa.de/")
@SOAPBinding(style = SOAPBinding.Style.RPC)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface Server {


    /**
     * 
     * @param arg0
     */
    @WebMethod
    @Action(input = "http://webservice.mpa.de/Server/receiveMessageRequest", output = "http://webservice.mpa.de/Server/receiveMessageResponse")
    public void receiveMessage(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0);

    /**
     * 
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(partName = "return")
    @Action(input = "http://webservice.mpa.de/Server/sendMessageRequest", output = "http://webservice.mpa.de/Server/sendMessageResponse")
    public String sendMessage();

    /**
     * 
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(partName = "return")
    @Action(input = "http://webservice.mpa.de/Server/downloadFileRequest", output = "http://webservice.mpa.de/Server/downloadFileResponse")
    public String downloadFile(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0);

    /**
     * 
     * @param arg1
     * @param arg0
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(partName = "return")
    @Action(input = "http://webservice.mpa.de/Server/uploadFileRequest", output = "http://webservice.mpa.de/Server/uploadFileResponse")
    public String uploadFile(
        @WebParam(name = "arg0", partName = "arg0")
        String arg0,
        @WebParam(name = "arg1", partName = "arg1")
        byte[] arg1);

    /**
     * 
     * @param arg0
     */
    @WebMethod
    @Action(input = "http://webservice.mpa.de/Server/runSearchesRequest", output = "http://webservice.mpa.de/Server/runSearchesResponse")
    public void runSearches(
        @WebParam(name = "arg0", partName = "arg0")
        SearchSettings arg0);

}
