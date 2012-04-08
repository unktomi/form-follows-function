/*
 * Copyright 2008-2009 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

/** Provides reflective access to F3 values and types.
 *
 * This packages defines a Java API (rather than a F3 API), 
 * so it can be used from both Java and F3 code.
 * A future F3 API may be layered on top of this.
 *
 * <h2>Context</h2>
 * The objects in this package are directly or indirectly created
 * from a {@link f3.reflect.F3Context F3Context}.
 * In the default case there is a single {@code F3Context} instance that
 * uses Java reflection.  You get one of these by doing:
 * <pre>
 * F3Local.Context ctx = F3Local.getContext();
 * </pre>
 * Alternatively, you can do:
 * <pre>
 * F3Context ctx = F3Context.getInstance();
 * </pre>
 * The latter is more abstract (as it supports proxying for remote VMs)
 * but the more specific {@code F3Local.Context} supports some extra
 * operations that only make sense for same-VM reflection.
 *
 * <h2>Values</h2>
 * The various reflection operations do not directly use
 * Java values.  Instead,
 * an {@link f3.reflect.F3ObjectValue} is a <q>handle</q> or
 * proxy for an <code>Object</code>.  This extra layer of indirection
 * isn't needed in many cases, but it is useful for remote invocation,
 * remote control, or in general access to data in a different VM.
 *
 * <h2>Object creation</h2>
 * To do the equivalent of the F3 code:
 * <blockquote><pre>
 * var x = ...;
 * var z = Foo { a: 10; b: bind x.y };
 * </pre></blockquote>
 * you can do:
 * <blockquote><pre>
 * F3Context rcontext = ...;
 * F3ClassType cls = rcontext.findClass(...);
 * F3ObjectValue x = ...;
 * F3ObjectValue z = cls.allocation();
 * z.initVar("a", ???);
 * z.bindVar("b", ???);
 * z.initialize();
 * </pre></blockquote>
 *
 * <h2>Sequence operations</h2>
 * <p>
 * Use {@link f3.reflect.F3SequenceBuilder} to create a new sequence.
 * <p>
 * To get the number of items in a sequence,
 * use {@link f3.reflect.F3Value#getItemCount ValueRef.getItemCount}.
 * To index into a sequence,
 * use {@link f3.reflect.F3Value#getItem ValueRef.getItem}.
 *
 * <h2>Design notes and issues</h2>
 * Some design principles, influenced by the "Mirrored reflection"
 * APIs (<a href="http://bracha.org/mirrors.pdf">Bracha and Ungar:
 * <cite>Mirrors: Design Principles for Meta-level Facilities
 * of Object-Oritented Programming Languages</cite>, OOPSLA 2004</a>),
 * and <a href="http://java.sun.com/j2se/1.5.0/docs/guide/jpda/jdi/">JDI</a> :
 * <ul>
 * <li>No explicit constructors in user code.
 * <li>Keep everything abstract, and allow indirection.
 * For example, we might be working on objects in the current VM,
 * or a remote VM.  We might not have objects at all - a subset of the same API
 * might be used for (say) reading from {@code .class} files.
 * <li>Hence the core classes are interfaces or abstract.
 * <li>On the other hand, we should avoid useless levels of indirection
 * or "service lookup".
 * </ul>
 *
 * <h2>Limitations</h2>
 * <ul>
 * <li>Error handling isn't very consistent - sometimes we return null, and
 * sometimes we throw an exception.
 * <li>We don't support bound functions properly.
 * </ul>
 */

package f3.reflect;
