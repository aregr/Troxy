package no.sb1.troxy.http.common;

public class DestinationAddr {
    private final String protocol;
    private final String host;
    private final String port;

    public DestinationAddr(String protocol, String host, String port) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DestinationAddr that = (DestinationAddr) o;

        if (protocol != null ? !protocol.equals(that.protocol) : that.protocol != null) return false;
        if (host != null ? !host.equals(that.host) : that.host != null) return false;
        return port != null ? port.equals(that.port) : that.port == null;
    }

    @Override
    public int hashCode() {
        int result = protocol != null ? protocol.hashCode() : 0;
        result = 31 * result + (host != null ? host.hashCode() : 0);
        result = 31 * result + (port != null ? port.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DestinationAddr{" +
                "protocol='" + protocol + '\'' +
                ", host='" + host + '\'' +
                ", port='" + port + '\'' +
                '}';
    }
}
