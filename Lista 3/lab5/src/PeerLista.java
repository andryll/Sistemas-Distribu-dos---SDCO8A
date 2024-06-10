/**
 * Lab05: Sistema P2P
 * 
 * André Luís de Oliveira (2270170) e Lucas Eduardo Pires Parra (2208490)
 * Ultima atualizacao: 10/06/2024
 */


public enum PeerLista {
    
    PEER1 {
        @Override
        public String getNome() {
            return "PEER1";
        }        
    },
    PEER2 {
        public String getNome() {
            return "PEER2";
        }        
    },
    PEER3 {
        public String getNome() {
            return "PEER3";
        }        
    },
    PEER4 {
        public String getNome() {
            return "PEER4";
        }        
    },
    PEER5 {
        public String getNome() {
            return "PEER5";
        }        
    };
	
    public String getNome(){
        return "NULO";
    }    
}
