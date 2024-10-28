package org.forrest.keycloak;

import org.keycloak.provider.ConfiguredProvider;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.validate.AbstractStringValidator;
import org.keycloak.validate.ValidationContext;
import org.keycloak.validate.ValidationError;
import org.keycloak.validate.ValidatorConfig;


import java.util.List;

public class RemoteValidatorProvider extends AbstractStringValidator implements ConfiguredProvider {
    public static final String ID = "remote-validator";
    public static final String REMOTE_VALIDATOR_URL = "url";
    public static final String AUTHORIZATION = "authorization";

    @Override
    public String getHelpText() {
        return "The validator service needs to return JSON format: {success: bool, message: string}";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ProviderConfigurationBuilder.create().property()
                .name(REMOTE_VALIDATOR_URL)
                .type(ProviderConfigProperty.STRING_TYPE)
                .label("The URL of the remote validator")
                .defaultValue("")
                .required(true)
                .add()
                .property()
                .name(AUTHORIZATION)
                .type(ProviderConfigProperty.STRING_TYPE)
                .label("Authorization")
                .defaultValue("")
                .required(false)
                .add().build();
    }

    @Override
    public <C> C getConfig() {
        return ConfiguredProvider.super.getConfig();
    }

    @Override
    protected void doValidate(String attributeValue, String attributeName, ValidationContext context, ValidatorConfig validatorConfig) {
        String url = validatorConfig.getString(REMOTE_VALIDATOR_URL);
        ValidatorService validatorService = new ValidatorService(url, validatorConfig.getString(AUTHORIZATION));
        ValidatorResponse resp = validatorService.call(attributeName, attributeValue);
        if (!resp.isSuccess()) {
            context.addError(new ValidationError(ID, attributeName, resp.getMessage(), attributeValue));
        }
    }

    @Override
    public String getId() {
        return ID;
    }
}
