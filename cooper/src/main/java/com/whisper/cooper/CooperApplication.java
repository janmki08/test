package com.whisper.cooper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.whisper.cooper.cooperation.oq.OQ;
import com.whisper.cooper.cooperation.oq.impl.OQImpl;
import com.whisper.cooper.cooperation.ot.OT;
import com.whisper.cooper.cooperation.ot.impl.CharSequenceOT;
import com.whisper.cooper.cooperation.store.DocumentStore;
import com.whisper.cooper.cooperation.store.impl.DocumentStoreImpl;
import com.whisper.cooper.dbmodel.Item;
import com.whisper.cooper.dbrepository.ItemRepository;
import com.whisper.cooper.document.controller.count.Collaborator;
import com.whisper.cooper.document.formatter.impl.CharSeqDocumentFormatter;
import com.whisper.cooper.relay.relayer.OperationRelayer;

@SpringBootApplication
@EnableMongoRepositories
public class CooperApplication implements CommandLineRunner {
	
	@Autowired
	ItemRepository itemRepo;

	public static void main(String[] args) {
		SpringApplication.run(CooperApplication.class, args);
	}
	
	@Override
	   public void run(String... args) throws Exception {
	      System.out.println("-------------CREATE GROCERY ITEMS------\n");
	      createGroceryItems();

	      System.out.println("\n------------SHOW ALL GROCERY ITEMS---\n");
	      showAllGroceryItems();
	   }

	   void createGroceryItems() {
	      System.out.println("Data creation started...");
	      itemRepo.save(new Item("Whole Wheat Biscuit", "Whole Wheat Biscuit", 5, "snacks"));
	      System.out.println("Data creation complete...");
	   }

	   public void showAllGroceryItems() {
	      itemRepo.findAll().forEach(item -> System.out.println(getItemDetails(item)));
	   }

	   public void getGroceryItemByName(String name) {
	      System.out.println("Getting item by name: " + name);
	      Item item = itemRepo.findItemByName(name);
	      System.out.println(getItemDetails(item));
	   }

	   public String getItemDetails(Item item) {
	      System.out.println("Item Name: " + item.getName() + ", \nQuantity: " + item.getQuantity() + ", \nItem Category: " + item.getCategory());
	      return "";
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
