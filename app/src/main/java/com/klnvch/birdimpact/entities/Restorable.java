package com.klnvch.birdimpact.entities;

import android.os.Bundle;

public interface Restorable {
	void onSave(Bundle bundle);
	void onRestore(Bundle bundle);
}
