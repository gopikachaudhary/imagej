//
// AbstractDataObject.java
//

/*
ImageJ software for multidimensional image processing and analysis.

Copyright (c) 2010, ImageJDev.org.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the names of the ImageJDev.org developers nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
*/

package imagej.data;

import imagej.data.event.DataObjectCreatedEvent;
import imagej.data.event.DataObjectDeletedEvent;
import imagej.data.roi.Overlay;
import imagej.event.Events;

/**
 * Base implementation of {@link DataObject}.
 * 
 * @author Curtis Rueden
 * @see Dataset
 * @see Overlay
 */
public abstract class AbstractDataObject implements DataObject {

	private int refs = 0;

	@Override
	public void register() {
		Events.publish(new DataObjectCreatedEvent(this));
	}

	@Override
	public void delete() {
		Events.publish(new DataObjectDeletedEvent(this));
	}

	@Override
	public void incrementReferences() {
		refs++;
		if (refs == 1) register();
	}

	@Override
	public void decrementReferences() {
		if (refs == 0)
			throw new IllegalArgumentException("should not decrement reference count when there are already 0 references");
		refs--;
		if (refs == 0) delete();
	}

}
