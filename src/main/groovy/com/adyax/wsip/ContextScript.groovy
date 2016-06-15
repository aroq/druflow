package com.adyax.wsip

import com.adyax.wsip.dsl.DeployDSL
import com.adyax.wsip.dsl.DeployScript
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.BooleanExpression
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.ElvisOperatorExpression
import org.codehaus.groovy.ast.expr.GStringExpression
import org.codehaus.groovy.ast.expr.MapEntryExpression
import org.codehaus.groovy.ast.expr.MapExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.NamedArgumentListExpression
import org.codehaus.groovy.ast.expr.PostfixExpression
import org.codehaus.groovy.ast.expr.PrefixExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression
import org.codehaus.groovy.ast.expr.TernaryExpression
import org.codehaus.groovy.ast.expr.TupleExpression
import org.codehaus.groovy.ast.expr.UnaryMinusExpression
import org.codehaus.groovy.ast.expr.UnaryPlusExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.SecureASTCustomizer

import static org.codehaus.groovy.syntax.Types.*

/**
 * Created by alex on 09.08.15.
 */
class ContextScript extends Base {

    def script

    String type

    def stepsToExecute = [:]

    def execute(stage, runs, site = '') {
        def runsCount = 0

        runs.each { run ->
            DeployDSL dsl = new DeployDSL(stage: stage, site: site)
            stepsToExecute[run] = []
            dsl.run = run
            dsl.context = context
            dsl.script = script
            Binding binding = new Binding(controller: dsl)

            def secure = new SecureASTCustomizer()

            secure.with {
                methodDefinitionAllowed = false

                importsWhitelist = []
                staticImportsWhitelist = []
                staticStarImportsWhitelist = ['java.lang.Math']

                tokensWhitelist = [
                      PLUS, MINUS, MULTIPLY, DIVIDE, MOD, POWER, PLUS_PLUS,
                      MINUS_MINUS, COMPARE_EQUAL, COMPARE_NOT_EQUAL,
                      COMPARE_LESS_THAN, COMPARE_LESS_THAN_EQUAL,
                      COMPARE_GREATER_THAN, COMPARE_GREATER_THAN_EQUAL, EQUAL, LOGICAL_OR
                ]

                constantTypesClassesWhiteList = [
                      Object, String, Integer, Float, Long, Double, BigDecimal,
                      Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Boolean.TYPE
                ]

                receiversClassesWhiteList = [
                      Object, Math, Integer, Float, Double, Long, BigDecimal
                ]

                statementsWhitelist = [
                      BlockStatement, ExpressionStatement, ReturnStatement, IfStatement
                ]

                expressionsWhitelist = [
                  BinaryExpression,       ConstantExpression,
                  MethodCallExpression,   StaticMethodCallExpression,
                  ArgumentListExpression, PropertyExpression,
                  UnaryMinusExpression,   UnaryPlusExpression,
                  PrefixExpression,       PostfixExpression,
                  TernaryExpression,      ElvisOperatorExpression,
                  BooleanExpression,      ClassExpression,
                  VariableExpression,     ClosureExpression,
                  TupleExpression,        NamedArgumentListExpression,
                  MapEntryExpression,     MapExpression,
                  GStringExpression
                ]
            }

            def config = new CompilerConfiguration(scriptBaseClass: DeployScript.class.name)
            config.addCompilationCustomizers(secure)

            GroovyShell shell = new GroovyShell(this.class.classLoader, binding, config)
            try {
                runsCount++
                debug("Processing script: ${script}")
                shell.evaluate(script)
                if (dsl.stepsToExecute) {
                    stepsToExecute[run] += dsl.stepsToExecute
                }
            }
            catch (MissingPropertyException e) {
                error(/Command "${e.property}" is not known/)
            }
            catch (InterruptedException e) {
                throw e
            }
            catch (Exception e) {
                error(e)
            }
        }
        stepsToExecute
    }

}
