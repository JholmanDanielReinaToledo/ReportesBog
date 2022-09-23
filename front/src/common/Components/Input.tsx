import { isDate, isEmpty, isString } from 'lodash';
import {
  FC,
  JSXElementConstructor,
  ReactElement,
  ReactNode,
} from 'react';
import {
  Popover,
  Input,
  Form,
  InputProps,
  FormItemProps,
} from 'antd';
import styles from '../../../styles/Fields.module.less';

const { Password } = Input;

const InputForm: FC<{
  formProps: FormItemProps<any>,
  inputProps?: InputProps,
  isRequired?: boolean,
  requiredMessage?: ReactElement<any, string | JSXElementConstructor<any>>,
  disableNormalize: boolean,
  title: any,
}> = ({
  formProps, inputProps, isRequired, requiredMessage, disableNormalize, title,
}) => (
  <Form.Item
    {...{
      ...formProps,
      initialValue: !(isString(formProps.initialValue) || isDate(formProps.initialValue))
        ? null
        : formProps.initialValue,
      rules: [
        {
          required: isRequired,
          message: requiredMessage || 'El campo es obligatorio',
        },
        ...(formProps.rules || []),
      ],
    }}
    normalize={(disableNormalize || inputProps?.type === 'date')
      ? undefined
      : (value) => (value || '').toUpperCase()}
  >
    <Input
      role="textbox"
      aria-label={title}
      aria-required={isRequired}
      className={
        inputProps && (isEmpty(inputProps.className) ? styles.input : inputProps.className)
      }
      size="large"
      {...inputProps}
    />
  </Form.Item>
);

const PasswordForm: FC<{
  formProps: FormItemProps<any>,
  inputProps?: InputProps,
  isRequired?: boolean,
  requiredMessage?: ReactElement<any, string | JSXElementConstructor<any>>,
  title: any;
}> = ({
  formProps, inputProps, isRequired, requiredMessage, title,
}) => (
  <Form.Item
    noStyle
    {...{
      ...formProps,
      initialValue: isString(formProps.initialValue)
        ? null
        : formProps.initialValue,
      rules: [
        {
          required: isRequired,
          message: requiredMessage || 'El campo es obligatorio',
        },
        ...(formProps.rules || []),
      ],
    }}
  >
    <Password
      role="textbox"
      aria-label={title}
      className={
        inputProps && (
          isEmpty(inputProps.className) ? styles.inputPassword : inputProps.className
        )
      }
      size="large"
      {...inputProps}
    />
  </Form.Item>
);

const InputPropio: FC<{
  descripcion?: string,
  formProps: FormItemProps,
  titulo: ReactNode,
  isRequired?: boolean,
  isPassword?: boolean,
  noAsterix?: boolean,
  requiredMessage?: ReactElement<any, string | JSXElementConstructor<any>>,
  disableNormalize?: boolean,
  titleClassName?: string,
} & InputProps> = ({
  descripcion,
  formProps = {},
  titulo,
  isRequired,
  requiredMessage,
  noAsterix,
  isPassword,
  disableNormalize = false,
  titleClassName,
  ...inputProps
}) => (
  <>
    {titulo && <p className={titleClassName || styles.inputTitle}>{titulo}</p>}
    {isRequired && !noAsterix && <em className={styles.required}>*</em>}
    {descripcion ? (
      <Popover title={null} content={descripcion}>
        {descripcion && ''}
        {isPassword
          ? (
            <PasswordForm
              title={titulo}
              inputProps={inputProps}
              formProps={formProps}
              isRequired={isRequired}
              requiredMessage={requiredMessage}
            />
          )
          : (
            <InputForm
              title={titulo}
              inputProps={inputProps}
              formProps={formProps}
              isRequired={isRequired}
              requiredMessage={requiredMessage}
              disableNormalize={disableNormalize}
            />
          )}
      </Popover>
    ) : (
      // eslint-disable-next-line react/jsx-no-useless-fragment
      <>
        {isPassword
          ? (
            <PasswordForm
              title={titulo}
              inputProps={inputProps}
              formProps={formProps}
              isRequired={isRequired}
              requiredMessage={requiredMessage}
            />
          )
          : (
            <InputForm
              title={titulo}
              inputProps={inputProps}
              formProps={formProps}
              isRequired={isRequired}
              requiredMessage={requiredMessage}
              disableNormalize={disableNormalize}
            />
          )}
      </>
    )}
  </>
);

export default InputPropio;
