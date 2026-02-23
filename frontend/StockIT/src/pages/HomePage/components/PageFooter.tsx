import { FOOTER_TEXTO } from '../constants';

export function PageFooter() {
  return (
    <footer className="text-center py-4 bg-white border-t border-gray-200 text-sm text-gray-400">
      {FOOTER_TEXTO}
    </footer>
  );
}
