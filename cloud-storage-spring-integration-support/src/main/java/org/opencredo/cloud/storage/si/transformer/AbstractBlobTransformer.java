/* Copyright 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opencredo.cloud.storage.si.transformer;

import org.opencredo.cloud.storage.BlobDetails;
import org.opencredo.cloud.storage.StorageOperations;
import org.springframework.integration.core.Message;
import org.springframework.util.Assert;

/**
 * @author Tomas Lukosius (tomas.lukosius@opencredo.com)
 * 
 */
public abstract class AbstractBlobTransformer<T> implements BlobTransformer<T> {

    private final StorageOperations template;
    private final boolean deleteBlob;

    public AbstractBlobTransformer(StorageOperations template) {
        this(template, false);
    }

    /**
     * @param template
     * @param deleteBlob
     */
    public AbstractBlobTransformer(StorageOperations template, boolean deleteBlob) {
        super();
        Assert.notNull(template, "Template must be specified");
        this.template = template;
        this.deleteBlob = deleteBlob;
    }

    /**
     * That is 'template method' pattern implementation. Extensions of this
     * class should not worry about deleting blob after it was downloaded and
     * transformed to some other form.
     * 
     * @param message
     * @return
     * @throws BlobTransformException
     * @see org.opencredo.cloud.storage.si.transformer.BlobTransformer#transform(org.springframework.integration.core.Message)
     */
    public Message<T> transform(Message<BlobDetails> message) throws BlobTransformException {
        Assert.notNull(message.getPayload(), "Transformer expects message payload");
        Message<T> result = doTransform(message);

        deleteBlobIfNeeded(message.getPayload().getContainerName(), message.getPayload().getName());

        return result;
    }

    /**
     * 
     * @param message
     *            SI message containing {@link BlobDetails} as payload.
     * @return SI message containing any type as message payload.
     * @throws BlobTransformException
     */
    protected abstract Message<T> doTransform(Message<BlobDetails> message) throws BlobTransformException;

    /**
     * @param containerName
     * @param name
     */
    public void deleteBlobIfNeeded(String containerName, String blobName) {
        if (deleteBlob) {
            template.deleteObject(containerName, blobName);
        }
    }

    /**
     * @return the template
     */
    public StorageOperations getTemplate() {
        return template;
    }

    /**
     * @return the deleteBlob
     */
    public boolean isDeleteBlob() {
        return deleteBlob;
    }
}
