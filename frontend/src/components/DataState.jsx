import Alert from './Alert.jsx';
import Button from './Button.jsx';

export default function DataState({ loading, error, empty, emptyMessage, onRetry, children }) {
  if (loading) {
    return (
      <p role="status" className="py-10 text-center text-sm text-muted-fg">
        Carregando…
      </p>
    );
  }
  if (error) {
    return (
      <div className="py-4">
        <Alert type="error">
          <p>{error}</p>
          {onRetry && (
            <Button className="mt-2" onClick={onRetry}>
              Tentar de novo
            </Button>
          )}
        </Alert>
      </div>
    );
  }
  if (empty) {
    return <p className="py-10 text-center text-sm text-muted-fg">{emptyMessage}</p>;
  }
  return children;
}
