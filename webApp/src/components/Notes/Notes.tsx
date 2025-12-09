import { useState } from 'react';
import { NotesManager, Note } from 'shared';
import './Notes.css';

// Helper function to convert Kotlin List to JavaScript Array
function toArray<T>(list: any): T[] {
  if (!list) {
    return [];
  }
  if (Array.isArray(list)) {
    return list;
  }
  // Try Array.from first (works for iterables)
  try {
    const array = Array.from(list as any) as T[];
    return array;
  } catch (e) {
    console.warn('Array.from failed, trying manual conversion:', e);
  }
  // Fallback: convert manually if needed
  const result: T[] = [];
  if (list) {
    // Try to iterate if it's iterable
    try {
      // Check if it has size property (Kotlin List)
      if (typeof (list as any).size === 'number') {
        const size = (list as any).size;
        for (let i = 0; i < size; i++) {
          const item = (list as any).get(i);
          if (item) {
            result.push(item);
          }
        }
      } else {
        // Try for...of
        for (const item of list as any) {
          result.push(item);
        }
      }
    } catch (e) {
      console.warn('Manual conversion failed:', e);
    }
  }
  return result;
}

export function Notes() {
  const [notesManager] = useState(() => new NotesManager());
  const [notes, setNotes] = useState<Note[]>(() => {
    const result = notesManager.getAllNotes();
    return toArray<Note>(result);
  });
  const [searchQuery, setSearchQuery] = useState('');
  const [showAddDialog, setShowAddDialog] = useState(false);
  const [editingNote, setEditingNote] = useState<Note | null>(null);

  const refreshNotes = () => {
    try {
      const result = notesManager.getAllNotes();
      const notesArray = toArray<Note>(result);
      console.log('Refreshing notes, count:', notesArray.length);
      setNotes(notesArray);
    } catch (error) {
      console.error('Error refreshing notes:', error);
    }
  };

  const displayedNotes = searchQuery.trim()
    ? toArray<Note>(notesManager.searchNotes(searchQuery))
    : notes;

  const handleAdd = (title: string, content: string, color: string) => {
    if (title.trim()) {
      try {
        if (editingNote) {
          const updated = notesManager.updateNote(editingNote.id, title, content, color);
          console.log('Updated note:', updated);
          if (updated) {
            // Update the note in state directly
            setNotes(prevNotes => {
              const updatedNotes = prevNotes.map(note => 
                note.id === editingNote.id ? updated : note
              );
              return updatedNotes.sort((a, b) => Number(b.updatedAt || 0) - Number(a.updatedAt || 0));
            });
          } else {
            refreshNotes();
          }
        } else {
          const added = notesManager.addNote(title, content, color);
          console.log('Added note:', added);
          if (added) {
            // Add the note to state directly
            setNotes(prevNotes => {
              const newNotes = [...prevNotes, added];
              return newNotes.sort((a, b) => Number(b.updatedAt || 0) - Number(a.updatedAt || 0));
            });
          } else {
            refreshNotes();
          }
        }
        setShowAddDialog(false);
        setEditingNote(null);
      } catch (error) {
        console.error('Error adding/updating note:', error);
        // Still refresh even on error to show current state
        refreshNotes();
      }
    }
  };

  const handleDelete = (id: string) => {
    notesManager.deleteNote(id);
    refreshNotes();
  };

  const handleEdit = (note: Note) => {
    setEditingNote(note);
    setShowAddDialog(true);
  };

  return (
    <div className="notes-container">
      <div className="notes-header">
        <h2>Notes ({notes.length})</h2>
        <button className="add-btn" onClick={() => setShowAddDialog(true)}>+ Add</button>
      </div>

      <input
        type="text"
        placeholder="Search notes..."
        value={searchQuery}
        onChange={(e) => setSearchQuery(e.target.value)}
        className="notes-search"
      />

      {displayedNotes.length === 0 ? (
        <div className="notes-empty">
          <p>{searchQuery.trim() ? 'No notes found' : 'No notes yet!'}</p>
          {!searchQuery.trim() && <p>Click the + button to add one</p>}
        </div>
      ) : (
        <div className="notes-grid">
          {displayedNotes.map((note) => (
            <div
              key={note.id}
              className="note-card"
              style={{ backgroundColor: note.color }}
            >
              <div className="note-content">
                <div className="note-title">{note.title}</div>
                {note.content && <div className="note-text">{note.content}</div>}
              </div>
              <div className="note-actions">
                <button
                  className="note-btn edit"
                  onClick={() => handleEdit(note)}
                  aria-label="Edit note"
                >
                  ✏️
                </button>
                <button
                  className="note-btn delete"
                  onClick={() => handleDelete(note.id)}
                  aria-label="Delete note"
                >
                  🗑️
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {showAddDialog && (
        <NoteDialog
          note={editingNote}
          onClose={() => {
            setShowAddDialog(false);
            setEditingNote(null);
          }}
          onSave={handleAdd}
        />
      )}
    </div>
  );
}

interface NoteDialogProps {
  note: Note | null;
  onClose: () => void;
  onSave: (title: string, content: string, color: string) => void;
}

function NoteDialog({ note, onClose, onSave }: NoteDialogProps) {
  const [title, setTitle] = useState(note?.title || '');
  const [content, setContent] = useState(note?.content || '');
  const [color, setColor] = useState(note?.color || '#FFFFFF');

  const colors = ['#FFB3BA', '#BAFFC9', '#BAE1FF', '#FFFFBA', '#FFDFBA', '#FFFFFF'];

  return (
    <div className="dialog-overlay" onClick={onClose}>
      <div className="dialog-content" onClick={(e) => e.stopPropagation()}>
        <h3>{note ? 'Edit Note' : 'Add New Note'}</h3>
        <input
          type="text"
          placeholder="Title"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          className="dialog-input"
          autoFocus
        />
        <textarea
          placeholder="Content"
          value={content}
          onChange={(e) => setContent(e.target.value)}
          className="dialog-textarea"
          rows={5}
        />
        <div className="color-picker">
          {colors.map((c) => (
            <button
              key={c}
              className={`color-option ${color === c ? 'selected' : ''}`}
              style={{ backgroundColor: c }}
              onClick={() => setColor(c)}
              aria-label={`Select color ${c}`}
            />
          ))}
        </div>
        <div className="dialog-buttons">
          <button className="dialog-btn cancel" onClick={onClose}>
            Cancel
          </button>
          <button
            className="dialog-btn confirm"
            onClick={() => onSave(title, content, color)}
          >
            {note ? 'Update' : 'Add'}
          </button>
        </div>
      </div>
    </div>
  );
}

