package lab.tdse.laboratory.Protocols.util;

@FunctionalInterface
public interface WebMethod {
    String execute(Request req, Response res);
}