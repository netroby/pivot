/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pivot.wtk.media.drawing;

import org.apache.pivot.collections.Sequence;

/**
 * Shape transform listener interface.
 */
public interface ShapeTransformListener {
    /**
     * Called when a transform has been inserted into a shape's transform
     * sequence.
     *
     * @param shape
     * @param index
     */
    public void transformInserted(Shape shape, int index);

    /**
     * Called when transforms have been removed from a shape's transform
     * sequence.
     *
     * @param shape
     * @param index
     * @param transforms
     */
    public void transformsRemoved(Shape shape, int index, Sequence<Shape.Transform> transforms);

    /**
     * Called when a transform has been updated in a shape's transform
     * sequence.
     *
     * @param transform
     */
    public void transformUpdated(Shape.Transform transform);
}
