package ru.malnev.gbcloud.client.command;

import ru.malnev.gbcloud.common.utils.Util;

import javax.enterprise.inject.spi.CDI;
import javax.enterprise.util.AnnotationLiteral;

@Keyword(Const.HELP_COMMAND_KEYWORD)
@Description(Const.HELP_COMMAND_DESCRIPTION)
public class HelpCommand extends AbstractCommand
{
    private static final AnnotationLiteral<Command> COMMAND_ANNOTATION = new AnnotationLiteral<Command>() {};

    @Override
    public void run()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("Supported commands are: ");
        builder.append(System.getProperty("line.separator"));
        CDI.current().select(COMMAND_ANNOTATION).forEach(commandBean ->
        {
            final Util.AnnotatedClass annotatedWithKeyword = Util.getAnnotation(Keyword.class, commandBean.getClass());
            final Util.AnnotatedClass annotatedWithDescription = Util.getAnnotation(Description.class, commandBean.getClass());
            final Util.AnnotatedClass annotatedWithArguments = Util.getAnnotation(Arguments.class, commandBean.getClass());

            if (annotatedWithKeyword == null) return;
            final Keyword keywordAnnotation = (Keyword) annotatedWithKeyword.getAnnotation();
            builder.append(keywordAnnotation.value());
            builder.append(" ");
            if (annotatedWithArguments != null)
            {
                final Arguments argumentsAnnotation = (Arguments) annotatedWithArguments.getAnnotation();
                for (final String argumentName : argumentsAnnotation.value())
                {
                    builder.append("<");
                    builder.append(argumentName);
                    builder.append("> ");
                }
            }

            if (annotatedWithDescription != null)
            {
                final Description descriptionAnnotation = (Description) annotatedWithDescription.getAnnotation();
                builder.append("- ");
                builder.append(descriptionAnnotation.value());
            }

            builder.append(System.getProperty("line.separator"));
        });

        System.out.println(builder.toString());
    }
}
