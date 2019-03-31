/*public class Main {
    public static void main(String[] args) {
        com.networkComponents.connectors.Port R1_eth0 = new com.networkComponents.connectors.Port();  //making ports and assigning them to newly created routers
        com.networkComponents.connectors.Port R1_eth1 = new com.networkComponents.connectors.Port();
        com.networkComponents.connectors.Port R2_eth0 = new com.networkComponents.connectors.Port();
        com.networkComponents.connectors.Port R2_eth1 = new com.networkComponents.connectors.Port();
        com.networkComponents.connectors.Port R3_eth0 = new com.networkComponents.connectors.Port();
        com.networkComponents.connectors.Port R3_eth1 = new com.networkComponents.connectors.Port();
        com.networkComponents.connectors.Port R4_eth0 = new com.networkComponents.connectors.Port();
        com.networkComponents.connectors.Port R5_eth0 = new com.networkComponents.connectors.Port();
        com.networkComponents.devices.router.Router R1 = new com.networkComponents.devices.router.Router("R1");
        com.networkComponents.devices.router.Router R2 = new com.networkComponents.devices.router.Router("R2");
        com.networkComponents.devices.router.Router R3 = new com.networkComponents.devices.router.Router("R3");
        com.networkComponents.devices.router.Router R4 = new com.networkComponents.devices.router.Router("R4");
        com.networkComponents.devices.router.Router R5 = new com.networkComponents.devices.router.Router("R5");
        R1.addNewPort(R1_eth0);
        R1.addNewPort(R1_eth1);
        R2.addNewPort(R2_eth0);
        R2.addNewPort(R2_eth1);
        R3.addNewPort(R3_eth0);
        R3.addNewPort(R3_eth1);
        R4.addNewPort(R4_eth0);
        R5.addNewPort(R5_eth0);
        R1_eth1.subscribe(R1);
        R1_eth0.subscribe(R1);
        R2_eth0.subscribe(R2);
        R2_eth1.subscribe(R2);
        R3_eth0.subscribe(R3);
        R3_eth1.subscribe(R3);
        R4_eth0.subscribe(R4);
        R5_eth0.subscribe(R5);

        com.networkComponents.connectors.BroadcastDomain A = new com.networkComponents.connectors.BroadcastDomain("A"); // making domains and connecting ports
        com.networkComponents.connectors.BroadcastDomain B = new com.networkComponents.connectors.BroadcastDomain("B");
        com.networkComponents.connectors.BroadcastDomain C = new com.networkComponents.connectors.BroadcastDomain("C");
        com.networkComponents.connectors.BroadcastDomain D = new com.networkComponents.connectors.BroadcastDomain("D");
        D.addPort(R1_eth1);
        A.addPort(R1_eth0);
        A.addPort(R2_eth0);
        B.addPort(R2_eth1);
        B.addPort(R3_eth0);
        C.addPort(R3_eth1);
        C.addPort(R4_eth0);
        C.addPort(R5_eth0);
        R1_eth1.create_link(D);
        R1_eth0.create_link(A);
        R2_eth0.create_link(A);
        R2_eth1.create_link(B);
        R3_eth0.create_link(B);
        R3_eth1.create_link(C);
        R4_eth0.create_link(C);
        R5_eth0.create_link(C);

        R1_eth0.set_address("192.168.1.1", "255.255.255.0"); // setting interface ip addresses
        R1_eth1.set_address("200.0.0.1", "255.255.255.0");
        R2_eth0.set_address("192.168.1.2", "255.255.255.0");
        R2_eth1.set_address("10.0.0.1", "255.255.255.0");
        R3_eth0.set_address("10.0.0.2", "255.255.255.0");
        R3_eth1.set_address("172.16.0.1", "255.255.255.0");
        R4_eth0.set_address("172.16.0.2", "255.255.255.0");
        R5_eth0.set_address("172.16.0.3", "255.255.255.0");

        R1.addRoutingTableEntry("10.0.0.0", "255.255.255.0", "192.168.1.2", 0); //making static routing entry
        R1.addRoutingTableEntry("172.16.0.0", "255.255.255.0", "192.168.1.2", 0);
        R2.addRoutingTableEntry("172.16.0.0", "255.255.255.0", "10.0.0.2", 1);
        R2.addRoutingTableEntry("200.0.0.0", "255.255.255.0", "192.168.1.1", 0);
        R3.addRoutingTableEntry("192.168.1.0", "255.255.255.0", "10.0.0.1", 0);
        R3.addRoutingTableEntry("200.0.0.0", "255.255.255.0", "10.0.0.1", 0);
        R4.addRoutingTableEntry("10.0.0.0", "255.255.255.0", "172.16.0.1", 0);
        R4.addRoutingTableEntry("192.168.1.0", "255.255.255.0", "172.16.0.1", 0);
        R4.addRoutingTableEntry("200.0.0.0", "255.255.255.0", "172.16.0.1", 0);
        R5.addRoutingTableEntry("10.0.0.0", "255.255.255.0", "172.16.0.1", 0);
        R5.addRoutingTableEntry("192.168.1.0", "255.255.255.0", "172.16.0.1", 0);
        R5.addRoutingTableEntry("200.0.0.0", "255.255.255.0", "172.16.0.1", 0);

        R1.start();
        R2.start();
        R3.start();
        R4.start();
        R5.start();


        com.networkComponents.connectors.Port PC1_eth0 = new com.networkComponents.connectors.Port();
        com.networkComponents.devices.Pc PC1 = new com.networkComponents.devices.Pc("PC1");
        PC1.addNewPort(PC1_eth0);
        D.addPort(PC1_eth0);
        PC1_eth0.create_link(D);
        PC1_eth0.set_address("200.0.0.2", "255.255.255.0");
        PC1.addDefaultGateway("200.0.0.1");
        PC1.start();

        PC1.ping("172.16.0.3");
        PC1.ping("172.16.0.3");
        PC1.ping("172.16.0.3");
        PC1.ping("172.16.0.3");
        //PC1.traceroute("172.16.0.3");
        //R1.ping("192.168.1.2");
        //R1.ping("10.0.0.2");
        //R1.ping("172.16.0.3");

        R1.shutdown();
        R2.shutdown();
        R3.shutdown();
        R4.shutdown();
        R5.shutdown();
        PC1.shutdown();

        Todo
        done routing dalej jesli pakiet nie moj
        done ogarnac czego nie działa ping 10.0.0.3
        done obsługa pakietu jesli dest adress nalezy do sieci directly connected
        done automatyczne wpisy do tablicy routingu jako directtly connected na podstawie maski interfejsu, BRAK next hopa
        done funkcja dodaj wpis ma zmieniac directly connected na false i hop count na ??
        done pingowanie samego siebie
        canceled exception przy ustawieniu adressu interfejsu jak adres sieci oraz maski innej niz ABC
        done dodac Pc czyli router tylko z ping i z default gateway
        done routery jako osobne wątki?
        done PC destination unreachable (brak gw) ?
        done DYNAMICZNY ROUTING
        done odpowiedz icmp destination unreachable
        done traceroute
        done ogarnac lepszy longest prefix match bo np dla adresu 11.0.0.0 R2 znajduje wpis (dla maski 7) a nie powinien
        done zmiana adresu na interface zmienia wpis w routing table
        done jesli port *usuniety* lub shutdownowany to wyczysc tablice routingu
        done usuwanie device zamyka thread
        done ustawienie static entry nei wlacza flush licznika
        done sprawdzanie poprawnosci adresow dla pc i port
        done przy dodawaniu entry sprawdzic czy port istnieje
        done ogarnac czego entry sie nie usuwa
        done ogarnac flush timery czy działaja
        done wylaczenie ripa, usuwa trasy
        done jeszcze validejty do remove static entry
        done NAPISAC SH RUN
        done ogarnac funkcje shutdown no shutdown dla pc
        done set address dla pc
        done klikniecie kilka razy w no sh psuje tablice routingu
        done getery gettery
        done validate do portu na pc
        done nie pinguj i nie traceroutuj kiedy off
        done output do nowego okna
        done trasy statyczne powinny nadpisywać ripa (lub coegzystować) [KOLIZJE przy FindEntry!!!!]
        done jesli pierwsze wpisana zostanie trasa statyczna a potem odpalony rip to entry ripowskie się nie zrobi (ale jebac)
        done rip bierze do rip info tylko directly connected !!!!!!!!!
        done bezposrednie podloczenie 2 pc (brak gw)
        done delete routera i pc usuwa porty i refernecje z br domain (na latwizne ustawia na nich sh)
        done zmiana BR domain w trakcie dzialania usuwa port ze starej i dodaje do nowej w pc i eth

        - ogarnac czego sie wykrzacza przy zmianie ip podczas dzialania ripa ???
        - fix status w sh run
        - FLUSH TIME POWINIEN SIE UPDATOWAC TYLKO KIEDY OTRZYMAM INFO OD NEX HOPA

        canceled zapis konfiguracji do pliku
        done zmiana adresu na porcie oraz sutdown flushuje rip (turn on / turn of ?)
        canceled po wylaczeniu interfejsu i zmianie adresu portu rip propaguje info o niedostepnosci sieci
    }
}
*/