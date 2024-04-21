package com.whisper.cooper.document.formatter;

import com.whisper.cooper.cooperation.model.TextOperation;

public interface DocumentFormatter {

    String applyOperation(TextOperation operation);

    String getText();

}
