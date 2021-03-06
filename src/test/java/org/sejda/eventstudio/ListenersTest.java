/* 
 * This file is part of the EventStudio source code
 * Created on 14/nov/2013
 * Copyright 2013 by Andrea Vacondio (andrea.vacondio@gmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.sejda.eventstudio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.sejda.eventstudio.Listener;
import org.sejda.eventstudio.Listeners;
import org.sejda.eventstudio.ReferenceStrength;
import org.sejda.eventstudio.Listeners.ListenerReferenceHolder;

/**
 * @author Andrea Vacondio
 * 
 */
public class ListenersTest {

    private Listeners victim;

    @Before
    public void setUp() {
        victim = new Listeners();
    }

    @Test
    public void add() {
        assertTrue(victim.nullSafeGetListeners(TestEvent.class).isEmpty());
        victim.add(TestEvent.class, new TestListener(), 0, ReferenceStrength.STRONG);
        assertFalse(victim.nullSafeGetListeners(TestEvent.class).isEmpty());
        assertEquals(1, victim.nullSafeGetListeners(TestEvent.class).size());
    }

    @Test
    public void addSameListener() {
        TestListener listener = new TestListener();
        victim.add(TestEvent.class, listener, 0, ReferenceStrength.STRONG);
        victim.add(TestEvent.class, listener, -1, ReferenceStrength.STRONG);
        assertEquals(2, victim.nullSafeGetListeners(TestEvent.class).size());
    }

    @Test
    public void addManyDiffenent() {
        victim.add(AnotherTestEvent.class, new AnotherTestListener(), 0, ReferenceStrength.STRONG);
        victim.add(TestEvent.class, new TestListener(), -1, ReferenceStrength.STRONG);
        victim.add(TestEvent.class, new SecondTestListener(), -1, ReferenceStrength.STRONG);
        assertEquals(2, victim.nullSafeGetListeners(TestEvent.class).size());
        assertEquals(1, victim.nullSafeGetListeners(AnotherTestEvent.class).size());
    }

    @Test
    public void remove() {
        TestListener listener = new TestListener();
        assertTrue(victim.nullSafeGetListeners(TestEvent.class).isEmpty());
        victim.add(TestEvent.class, listener, 0, ReferenceStrength.STRONG);
        assertFalse(victim.nullSafeGetListeners(TestEvent.class).isEmpty());
        victim.remove(TestEvent.class, listener);
        assertTrue(victim.nullSafeGetListeners(TestEvent.class).isEmpty());
    }

    @Test
    public void removeMany() {
        TestListener listener = new TestListener();
        SecondTestListener listener2 = new SecondTestListener();
        AnotherTestListener anotherListener = new AnotherTestListener();
        victim.add(TestEvent.class, listener, 0, ReferenceStrength.STRONG);
        victim.add(TestEvent.class, listener2, 0, ReferenceStrength.WEAK);
        victim.add(AnotherTestEvent.class, anotherListener, 0, ReferenceStrength.SOFT);
        assertFalse(victim.nullSafeGetListeners(TestEvent.class).isEmpty());
        assertFalse(victim.nullSafeGetListeners(AnotherTestEvent.class).isEmpty());
        assertTrue(victim.remove(TestEvent.class, listener2));
        assertTrue(victim.remove(AnotherTestEvent.class, anotherListener));
        assertTrue(victim.nullSafeGetListeners(AnotherTestEvent.class).isEmpty());
        assertEquals(1, victim.nullSafeGetListeners(TestEvent.class).size());
    }

    @Test
    public void removeHolder() {
        TestListener listener = new TestListener();
        assertTrue(victim.nullSafeGetListeners(TestEvent.class).isEmpty());
        victim.add(TestEvent.class, listener, 0, ReferenceStrength.STRONG);
        for (ListenerReferenceHolder holder : victim.nullSafeGetListeners(TestEvent.class)) {
            assertTrue(victim.remove(TestEvent.class, holder));
        }
        assertTrue(victim.nullSafeGetListeners(TestEvent.class).isEmpty());
    }

    @Test
    public void falseRemove() {
        TestListener listener = new TestListener();
        AnotherTestListener anotherListener = new AnotherTestListener();
        victim.add(TestEvent.class, listener, 0, ReferenceStrength.STRONG);
        assertFalse(victim.remove(AnotherTestEvent.class, anotherListener));
    }

    private static class AnotherTestListener implements Listener<AnotherTestEvent> {
        public void onEvent(AnotherTestEvent event) {
            // nothing
        }
    }

    private static class SecondTestListener implements Listener<TestEvent> {
        public void onEvent(TestEvent event) {
            // nothing
        }
    }

    private static class TestListener implements Listener<TestEvent> {
        public void onEvent(TestEvent event) {
            // nothing
        }
    }

    private static class TestEvent {
        // nothing
    }

    private static class AnotherTestEvent {
        // nothing
    }

}
