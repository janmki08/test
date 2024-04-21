package com.whisper.cooper.cooperation.oq;

import com.whisper.cooper.cooperation.model.OQInPayload;

public interface OQ {
    void enqueue(OQInPayload inPayload);
}
