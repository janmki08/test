package com.whisper.cooper.dbmodel;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document("groceryitems")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Item {

	@Id
	private String id;
	private String name;
	private int quantity;
	private String category;
}
