/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.example.galaxytracing.agent.annocation.processor;

import com.google.auto.service.AutoService;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import org.example.galaxytracing.agent.annocation.TracingAgent;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * TracingAgent Processor.
 *
 * @author JiekerTime
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("org.example.galaxytracing.agent.annocation.TracingAgent")
public final class TracingAgentProcessor extends AbstractProcessor {
    
    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        TreeMaker treeMaker = TreeMaker.instance(context);
        Names names = Names.instance(context);
        if (!roundEnv.processingOver()) {
            roundEnv.getElementsAnnotatedWith(TracingAgent.class).stream().map(element -> JavacTrees.instance(processingEnv)
                    .getTree(element)).forEach(tree -> tree.accept(new TreeTranslator() {
                @Override
                public void visitClassDef(final JCTree.JCClassDecl jcClassDecl) {
                    JCTree agentVar = treeMaker.VarDef(
                            treeMaker.Modifiers(Flags.PUBLIC | Flags.STATIC | Flags.FINAL),
                            names.fromString("tracingAgent"),
                            treeMaker.Ident(names.fromString(org.example.galaxytracing.agent.TracingAgent.class.getSimpleName())),
                            treeMaker.NewClass(
                                    null,
                                    List.nil(),
                                    treeMaker.Ident(names.fromString(org.example.galaxytracing.agent.TracingAgent.class.getSimpleName())),
                                    List.nil(),
                                    null
                            )
                    );
                    jcClassDecl.defs = jcClassDecl.defs.append(agentVar);
                    super.visitClassDef(jcClassDecl);
                }
            }));
        }
        return true;
    }
}
