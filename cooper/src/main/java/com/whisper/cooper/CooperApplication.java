package com.whisper.cooper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.whisper.cooper.cooperation.oq.OQ;
import com.whisper.cooper.cooperation.oq.impl.OQImpl;
import com.whisper.cooper.cooperation.ot.OT;
import com.whisper.cooper.cooperation.ot.impl.CharSequenceOT;
import com.whisper.cooper.cooperation.store.DocumentStore;
import com.whisper.cooper.cooperation.store.impl.DocumentStoreImpl;
import com.whisper.cooper.document.controller.count.Collaborator;
import com.whisper.cooper.document.formatter.impl.CharSeqDocumentFormatter;
import com.whisper.cooper.relay.relayer.OperationRelayer;

@SpringBootApplication
//@EnableMongoRepositories
public class CooperApplication /*implements CommandLineRunner */{

	public static void main(String[] args) {
		SpringApplication.run(CooperApplication.class, args);
	}
	
    @Bean
    public OQ getOQ() {
        return new OQImpl();
    }

    @Bean
    public OperationRelayer getOperationRelayer() {
        return new OperationRelayer();
    }
	
    @Bean
    public OT getOT() {
        return new CharSequenceOT();
    }

    @Bean
    public DocumentStore getDocumentStore() {
        return new DocumentStoreImpl(CharSeqDocumentFormatter::new);
    }

    @Bean
    public Collaborator getCollaboratorCountNotifier() {
        return new Collaborator();
    }
}
