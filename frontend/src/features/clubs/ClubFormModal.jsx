import { createClub } from '../../api/clubs.js';
import Button from '../../components/Button.jsx';
import { TextInput } from '../../components/Field.jsx';
import Modal from '../../components/Modal.jsx';
import { useAction } from '../../hooks/useAction.js';
import { useForm } from '../../hooks/useForm.js';
import { checkText, collectErrors } from '../../lib/validators.js';

const ACRONYM_PATTERN = /^[A-Za-z]{3}$/;

const validate = (values) =>
  collectErrors({
    name: checkText(values.name, 'o nome do clube', 3, 60),
    acronym: ACRONYM_PATTERN.test(values.acronym.trim()) ? null : 'Use exatamente 3 letras, como AGU.',
  });

export default function ClubFormModal({ onClose, onCreated }) {
  const { run, pending } = useAction();
  const form = useForm({ name: '', acronym: '' }, validate);

  const save = (values) =>
    run(() => createClub({ name: values.name.trim(), acronym: values.acronym.trim().toUpperCase() }), {
      success: 'Clube criado.',
      onSuccess: () => {
        onCreated();
        onClose();
      },
    });

  return (
    <Modal title="Criar clube" onClose={onClose}>
      <form noValidate onSubmit={form.submit(save)} className="flex flex-col gap-3">
        <TextInput
          id="club-name"
          label="Nome"
          placeholder="Ex.: Águias FC"
          value={form.values.name}
          error={form.errors.name}
          onChange={(event) => form.setField('name', event.target.value)}
        />
        <TextInput
          id="club-acronym"
          label="Sigla"
          placeholder="Ex.: AGU"
          maxLength={3}
          value={form.values.acronym}
          error={form.errors.acronym}
          hint="Três letras que aparecem no nome, na mesma ordem."
          onChange={(event) => form.setField('acronym', event.target.value.toUpperCase())}
        />
        <div className="mt-2 flex justify-end gap-2">
          <Button onClick={onClose}>Cancelar</Button>
          <Button type="submit" variant="primary" loading={pending}>
            Criar clube
          </Button>
        </div>
      </form>
    </Modal>
  );
}
