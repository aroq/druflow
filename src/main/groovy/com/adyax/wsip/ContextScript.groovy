package com.adyax.wsip

import com.adyax.wsip.dsl.DeployDSL
import com.adyax.wsip.dsl.DeployScript
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.SecureASTCustomizer
import org.codehaus.groovy.syntax.Types

/**
 * Created by alex on 09.08.15.
 */
class ContextScript extends Base {

    def script

    String type

    def execute(stage, runs, site = '') {
        def runsCount = 0

        runs.each { run ->
            DeployDSL dsl = new DeployDSL(stage: stage, site: site)
            dsl.run = run
            dsl.context = context
            Binding binding = new Binding(controller: dsl)

            def secure = new SecureASTCustomizer()

            secure.with {
                methodDefinitionAllowed = false

                importsWhitelist = []
                staticImportsWhitelist = []
                staticStarImportsWhitelist = ['java.lang.Math']

                tokensWhitelist = [
                      Types.PLUS, Types.MINUS, Types.MULTIPLY, Types.DIVIDE, Types.MOD, Types.POWER, Types.PLUS_PLUS,
                      Types.MINUS_MINUS, Types.COMPARE_EQUAL, Types.COMPARE_NOT_EQUAL,
                      Types.COMPARE_LESS_THAN, Types.COMPARE_LESS_THAN_EQUAL,
                      Types.COMPARE_GREATER_THAN, Types.COMPARE_GREATER_THAN_EQUAL,
                ]

                constantTypesClassesWhiteList = [
                      Object, String, Integer, Float, Long, Double, BigDecimal,
                      Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE
                ]

                receiversClassesWhiteList = [
                      Object, Math, Integer, Float, Double, Long, BigDecimal
                ]

                statementsWhitelist = [
                      BlockStatement, ExpressionStatement, ReturnStatement
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
                ]
            }

            def config = new CompilerConfiguration(scriptBaseClass: DeployScript.class.name)
            config.addCompilationCustomizers(secure)

            GroovyShell shell = new GroovyShell(this.class.classLoader, binding, config)
            try {
                runsCount++
                debug("Executing script: ${script}")
                debug("Run #${runsCount}: environment: ${run}, stage: ${stage}")
                shell.evaluate(script)
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
    }

}
