const VARIANTS = {
  primary: 'bg-primary text-white hover:bg-primary-hover',
  outline: 'border border-line bg-white hover:bg-muted',
  ghost: 'hover:bg-muted',
  destructive: 'bg-danger text-white hover:opacity-90',
};

export default function Button({ variant = 'outline', loading = false, disabled, className = '', children, ...props }) {
  return (
    <button
      type="button"
      disabled={disabled || loading}
      aria-busy={loading || undefined}
      className={`inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-lg px-3 py-2 text-sm font-medium disabled:cursor-not-allowed disabled:opacity-50 ${VARIANTS[variant]} ${className}`}
      {...props}
    >
      {children}
    </button>
  );
}
