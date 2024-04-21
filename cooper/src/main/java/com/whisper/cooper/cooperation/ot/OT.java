package com.whisper.cooper.cooperation.ot;

import com.whisper.cooper.cooperation.model.TextOperation;

public interface OT {
    TextOperation[] transform(TextOperation op1, TextOperation op2);
}
