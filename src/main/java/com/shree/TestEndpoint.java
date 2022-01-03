package com.shree;

import java.io.*;
import java.math.BigInteger;
import java.net.URI;
import java.util.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.*;
import javax.xml.datatype.*;

import org.mitre.stix.stix_1.STIXPackage;
import org.mitre.taxii.*;
import org.mitre.taxii.client.HttpClient;
import org.mitre.taxii.messages.TaxiiXml;
import org.mitre.taxii.messages.xml11.*;

@WebServlet("/TAXII")
@Path("/taxi")
public class TestEndpoint extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        STIX st = new STIX();
        STIXPackage stixPackage = st.stix();
        System.out.println(stixPackage.toXMLString());
        String x = "<Discovery_Request xmlns=\"http://taxii.mitre.org/messages/taxii_xml_binding-1.1\" message_id=\"1\"/>";
        String x1 = "<taxii_11:Poll_Request xmlns:taxii_11=\"http://taxii.mitre.org/messages/taxii_xml_binding-1.1\" message_id=\"42158\" collection_name=\"default\"> <taxii_11:Exclusive_Begin_Timestamp>2014-12-19T00:00:00Z</taxii_11:Exclusive_Begin_Timestamp> <taxii_11:Inclusive_End_Timestamp>2014-12-19T12:00:00Z</taxii_11:Inclusive_End_Timestamp><taxii_11:Poll_Parameters allow_asynch=\"false\"><taxii_11:Response_Type>FULL</taxii_11:Response_Type></taxii_11:Poll_Parameters></taxii_11:Poll_Request>";
        discovery(request, x);
        poll(request, x1);

    }

    /*
     * Example request headers User-Agent: java-taxii.httpclient content-type:
     * application/xml accept: application/xml x-taxii-accept:
     * urn:taxii.mitre.org:message:xml:1.1 x-taxii-content-type:
     * urn:taxii.mitre.org:message:xml:1.1 x-taxii-services:
     * urn:taxii.mitre.org:services:1.1 x-taxii-protocol:
     * urn:taxii.mitre.org:protocol:http:1.0
     */

    // Are these thread-safe???
    private ObjectFactory factory = new ObjectFactory();
    private TaxiiXmlFactory txf = new TaxiiXmlFactory();
    private TaxiiXml taxiiXml = txf.createTaxiiXml();

    @POST
    @Path("discovery")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public Response discovery(@Context HttpServletRequest request, String x) {

        try {
            printHeaders(request);

            System.out.println("---------- Request:");
            DiscoveryRequest discoveryRequest = (DiscoveryRequest) getRequestObject(x);
            System.out.println(toXml(discoveryRequest));

            System.out.println("---------- Response:");
            List services = new ArrayList<>();
            services.add(factory.createServiceInstanceType().withServiceType(ServiceTypeEnum.POLL).withAddress("/poll")
                    .withAvailable(true).withProtocolBinding(Versions.VID_TAXII_HTTP_10)
                    .withServiceVersion(Versions.VID_TAXII_SERVICES_11).withMessageBindings(Versions.VID_TAXII_XML_11)
                    .withMessage("Super awesome data comes from this service").withContentBindings(
                            factory.createContentBindingIDType().withBindingId(ContentBindings.CB_STIX_XML_111)));

            DiscoveryResponse discoveryResponse = factory.createDiscoveryResponse()
                    .withInResponseTo(discoveryRequest.getMessageId()).withMessageId(MessageHelper.generateMessageId())
                    .withServiceInstances(services);

            String responseString = toXml(discoveryResponse);
            System.out.println(taxiiXml.marshalToString(discoveryResponse, true));

            return generateResponse(responseString, request);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("poll")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public Response poll(@Context HttpServletRequest request, String x) {

        try {
            printHeaders(request);

            System.out.println("---------- Request:");
            PollRequest pollRequest = (PollRequest) getRequestObject(x);
            System.out.println(toXml(pollRequest));
            String type = pollRequest.getPollParameters().getResponseType().equals(ResponseTypeEnum.FULL) ? "FULL"
                    : "COUNT ONLY";
            System.out.println("Response: " + type);
            System.out.println("Collection:" + pollRequest.getCollectionName());
            System.out.println("Start: " + (pollRequest.getExclusiveBeginTimestamp() != null
                    ? pollRequest.getExclusiveBeginTimestamp().toXMLFormat()
                    : "(none)"));
            System.out.println("End: " + (pollRequest.getInclusiveEndTimestamp() != null
                    ? pollRequest.getInclusiveEndTimestamp().toXMLFormat()
                    : "(none)"));

            System.out.println("---------- Response:");
            PollResponse pollResponse = factory.createPollResponse().withInResponseTo(pollRequest.getMessageId())
                    .withMessageId(MessageHelper.generateMessageId())
                    .withCollectionName(pollRequest.getCollectionName())
                    .withRecordCount(
                            factory.createRecordCountType().withValue(BigInteger.valueOf(9999)).withPartialCount(false))
                    .withExclusiveBeginTimestamp(pollRequest.getExclusiveBeginTimestamp())
                    .withInclusiveEndTimestamp(pollRequest.getInclusiveEndTimestamp())
                    .withContentBlocks(factory.createContentBlock()
                            .withContentBinding(
                                    factory.createContentInstanceType().withBindingId(ContentBindings.CB_STIX_XML_111))
                            .withContent(factory.createAnyMixedContentType()
                                    .withContent("Content Block Stuff Goes Here, STIX for example"))
                            .withTimestampLabel(getTimestamp(null)).withMessage("Here's your data"));

            String responseString = toXml(pollResponse);
            System.out.println(taxiiXml.marshalToString(pollResponse, true));

            return generateResponse(responseString, request);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Response generateResponse(String responseString, HttpServletRequest request) throws Exception {
        return Response.ok(responseString).header(HttpClient.HEADER_X_TAXII_PROTOCOL, getProtocol(request))
                .header(HttpClient.HEADER_X_TAXII_CONTENT_TYPE, Versions.VID_TAXII_XML_11)
                .header(HttpClient.HEADER_X_TAXII_SERVICES, Versions.VID_TAXII_SERVICES_11).build();
    }

    private String getProtocol(HttpServletRequest request) throws Exception {
        String scheme = new URI(request.getRequestURL().toString()).getScheme();
        if (scheme != null && scheme.equalsIgnoreCase("https")) {
            return Versions.VID_TAXII_HTTPS_10;
        } else {
            return Versions.VID_TAXII_HTTP_10;
        }
    }

    private void printHeaders(HttpServletRequest request) {
        System.out.println("--------------------------------------------");
        List headerNames = Collections.list(request.getHeaderNames());
        for (Object name : headerNames) {
            System.out.println(name + ": " + request.getHeader(name.toString()));
        }
    }

    private String toXml(Object discoveryResponse) throws Exception {
        final Marshaller m = taxiiXml.createMarshaller(false);
        m.setProperty(Marshaller.JAXB_FRAGMENT, true); // Don't generate xml declaration.
        final StringWriter sw = new StringWriter();
        m.marshal(discoveryResponse, sw);
        return sw.toString();
    }

    private Object getRequestObject(String x) throws Exception {
        Unmarshaller um = taxiiXml.getJaxbContext().createUnmarshaller();
        return um.unmarshal(new StringReader(x));
    }

    private XMLGregorianCalendar getTimestamp(Date dte) throws Exception {
        if (dte == null) {
            dte = new Date();
        }
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(dte);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
    }
}