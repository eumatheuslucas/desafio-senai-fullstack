const CONTROL = 'w-full rounded-lg border border-[#919199] bg-white px-3 py-2 text-sm disabled:bg-muted disabled:text-muted-fg';

export function Field({ id, label, error, hint, children }) {
  return (
    <div className="flex flex-col gap-1">
      <label htmlFor={id} className="text-sm font-medium">
        {label}
      </label>
      {children}
      {hint && !error && <p className="text-xs text-muted-fg">{hint}</p>}
      {error && (
        <p id={`${id}-error`} role="alert" className="text-sm text-danger">
          {error}
        </p>
      )}
    </div>
  );
}

export function TextInput({ id, label, error, hint, ...props }) {
  return (
    <Field id={id} label={label} error={error} hint={hint}>
      <input
        id={id}
        className={`${CONTROL} ${error ? 'border-danger' : ''}`}
        aria-invalid={error ? true : undefined}
        aria-describedby={error ? `${id}-error` : undefined}
        {...props}
      />
    </Field>
  );
}

export function SelectInput({ id, label, error, hint, children, ...props }) {
  return (
    <Field id={id} label={label} error={error} hint={hint}>
      <select
        id={id}
        className={`${CONTROL} ${error ? 'border-danger' : ''}`}
        aria-invalid={error ? true : undefined}
        aria-describedby={error ? `${id}-error` : undefined}
        {...props}
      >
        {children}
      </select>
    </Field>
  );
}
